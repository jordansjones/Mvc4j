package com.nextmethod.web.mvc;

import com.google.common.base.Ascii;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import com.nextmethod.TypeHelpers;
import com.nextmethod.web.InvalidOperationException;
import com.nextmethod.web.mvc.annotation.ActionNameSelector;
import com.nextmethod.web.mvc.annotation.IActionMethodSelector;
import com.nextmethod.web.mvc.annotation.IActionNameSelector;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

/**
 *
 */
final class ActionMethodSelector {

	private final Class<? extends IController> controllerCls;
	private static final Predicate<Method> IsValidActionMethod = createIsValidActionMethodPredicate();
	private static final Predicate<Method> IsMethodDecoratedWithAliasingAnnotation = createIsMethodDecoratedWithAliasingAnnotationPredicate();

	private Method[] aliasedMethods;
	private Multimap<String, Method> nonAliasedMethods;

	public ActionMethodSelector(final Class<? extends IController> controllerCls) {
		this.controllerCls = controllerCls;
		populateLookupTables();
	}

	public Method findActionMethod(final ControllerContext controllerContext, final String actionName) {
		final List<Method> matchingAliasedMethod = getMatchingAliasedMethod(controllerContext, actionName);
		final String nameKey = getMethodNameKey(actionName);
		if (nonAliasedMethods.containsKey(nameKey))
			matchingAliasedMethod.addAll(nonAliasedMethods.get(nameKey));

		final List<Method> methods = runSelectionFilters(controllerContext, matchingAliasedMethod);
		switch (methods.size()) {
			case 0:
				return null;

			case 1:
				return methods.get(0);

			default:
				// TODO: This
				throw new InvalidOperationException("Blah blah");
		}
	}

	private void populateLookupTables() {
		final Set<Method> declaredMethods = Sets.newHashSet(controllerCls.getDeclaredMethods());
		final Iterable<Method> actionMethods = Iterables.filter(declaredMethods, IsValidActionMethod);

		final Iterable<Method> aliasedMethods = Iterables.filter(actionMethods, IsMethodDecoratedWithAliasingAnnotation);
		this.aliasedMethods = Iterables.toArray(aliasedMethods, Method.class);

		final Iterable<Method> nonAliasedMethods = Iterables.filter(actionMethods, Predicates.not(IsMethodDecoratedWithAliasingAnnotation));
		this.nonAliasedMethods = HashMultimap.create();
		for (Method method : nonAliasedMethods) {
			this.nonAliasedMethods.put(getMethodNameKey(method.getName()), method);
		}
	}

	private static String getMethodNameKey(final String methodName) {
		final String s = Strings.nullToEmpty(methodName);
		return Ascii.toUpperCase(s);
	}

	List<Method> getMatchingAliasedMethod(final ControllerContext controllerContext, final String actionName) {
		// find all aliased methods which are opting in to this request
		// to opt in, all annotation defined on the method must return true
		final List<Method> methods = Lists.newArrayList();

		final Class<ActionNameSelector> actionNameSelectorClass = ActionNameSelector.class;

		for (Method method : aliasedMethods) {
			boolean isMatch = true;
			for (ActionNameSelector annotation : TypeHelpers.getMethodAnnotations(method, actionNameSelectorClass)) {
				if (!invokeActionNameSelector(annotation, controllerContext, actionName, method)) {
					isMatch = false;
				}
			}

			if (isMatch) {
				methods.add(method);
			}
		}

		return methods;
	}

	private static List<Method> runSelectionFilters(final ControllerContext controllerContext, final List<Method> methods) {
		// remove all methods which are opting out of this request
		// to opt out, at least one attribute defined on the method must return false

		final List<Method> matchesWithSelection = Lists.newArrayList();
		final List<Method> matchesWithoutSelection = Lists.newArrayList();

		for (Method method : methods) {
			final List<com.nextmethod.web.mvc.annotation.ActionMethodSelector> a = TypeHelpers.getMethodAnnotations(method, com.nextmethod.web.mvc.annotation.ActionMethodSelector.class);
			if (a.isEmpty()) {
				matchesWithoutSelection.add(method);
			} else {
				boolean isMatch = true;
				for (com.nextmethod.web.mvc.annotation.ActionMethodSelector annotation : a) {
					if (!invokeActionMethodSelector(annotation, controllerContext, method)) {
						isMatch = false;
					}
				}
				if (isMatch)
					matchesWithSelection.add(method);
			}
		}

		// if a matching action method had a selection attribute, consider it more specific than a matching action method
		// without a selection attribute
		return !matchesWithSelection.isEmpty() ? matchesWithSelection : matchesWithoutSelection;
	}

	private static boolean invokeActionNameSelector(final ActionNameSelector annotation, final ControllerContext controllerContext, final String actionName, final Method actionMethod) {
		if (annotation.value() != null) {
			try {
				final IActionNameSelector selector = annotation.value().newInstance();
				if (selector.isValidName(controllerContext, actionName, actionMethod)) {
					return true;
				}
			}
			catch (Exception ignored) {
			}
		}
		return false;
	}

	private static boolean invokeActionMethodSelector(final com.nextmethod.web.mvc.annotation.ActionMethodSelector annotation, final ControllerContext controllerContext, final Method actionMethod) {
		if (annotation.value() != null) {
			try {
				final IActionMethodSelector selector = annotation.value().newInstance();
				if (selector.isValidForRequest(controllerContext, actionMethod)) {
					return true;
				}
			}
			catch (Exception ignored) {
			}
		}
		return false;
	}

	private static final Class<Controller> ControllerClass = Controller.class;

	private static Predicate<Method> createIsValidActionMethodPredicate() {
		return new Predicate<Method>() {
			@Override
			public boolean apply(@Nullable final Method input) {
				return input != null
					&& Modifier.isPublic(input.getModifiers())
					&& !Modifier.isNative(input.getModifiers())
					&& !Modifier.isStrict(input.getModifiers())
					&& ControllerClass.isAssignableFrom(input.getDeclaringClass());
			}
		};
	}

	private static Predicate<Method> createIsMethodDecoratedWithAliasingAnnotationPredicate() {
		return new Predicate<Method>() {
			@Override
			public boolean apply(@Nullable final Method input) {
				return input != null
					&& TypeHelpers.methodAnnotatedWith(input, ActionNameSelector.class);
			}
		};
	}
}
