package nextmethod.web.mvc;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import nextmethod.Idx;
import nextmethod.annotations.TODO;
import nextmethod.reflection.AnnotationInfo;
import nextmethod.reflection.ClassInfo;
import nextmethod.reflection.MethodInfo;
import nextmethod.web.InvalidOperationException;
import nextmethod.web.mvc.annotations.ActionNameSelector;
import nextmethod.web.mvc.annotations.IActionMethodSelector;
import nextmethod.web.mvc.annotations.IActionNameSelector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static nextmethod.reflection.TypeOfHelper.typeOf;

/**
 *
 */
@TODO
final class ActionMethodSelector {

	private static final Predicate<MethodInfo> IsValidActionMethod = createIsValidActionMethodPredicate();
	private static final Predicate<MethodInfo> IsMethodDecoratedWithAliasingAnnotation = createIsMethodDecoratedWithAliasingAnnotationPredicate();

	private final ClassInfo<? extends IController> controllerCls;

	private Iterable<MethodInfo> aliasedMethods;
	private Multimap<Idx, MethodInfo> nonAliasedMethods;

	public ActionMethodSelector(final ClassInfo<? extends IController> controllerCls) {
		this.controllerCls = controllerCls;
		populateLookupTables();
	}

	public MethodInfo findActionMethod(final ControllerContext controllerContext, final String actionName) {
		final List<MethodInfo> matchingAliasedMethod = getMatchingAliasedMethod(controllerContext, actionName);
		final Idx nameKey = Idx.of(actionName);
		if (nonAliasedMethods.containsKey(nameKey))
			matchingAliasedMethod.addAll(nonAliasedMethods.get(nameKey));

		final List<MethodInfo> methods = runSelectionFilters(controllerContext, matchingAliasedMethod);
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
		final ImmutableCollection<MethodInfo> declaredMethods = controllerCls.getMethods();
		final Iterable<MethodInfo> actionMethods = Iterables.filter(declaredMethods, IsValidActionMethod);

		this.aliasedMethods = Iterables.filter(actionMethods, IsMethodDecoratedWithAliasingAnnotation);

		final Iterable<MethodInfo> nonAliasedMethods = Iterables.filter(actionMethods, Predicates.not(IsMethodDecoratedWithAliasingAnnotation));
		this.nonAliasedMethods = HashMultimap.create();
		for (MethodInfo method : nonAliasedMethods) {
			this.nonAliasedMethods.put(Idx.of(method.getName()), method);
		}
	}

	List<MethodInfo> getMatchingAliasedMethod(final ControllerContext controllerContext, final String actionName) {
		// find all aliased methods which are opting in to this request
		// to opt in, all annotation defined on the method must return true
		final List<MethodInfo> methods = Lists.newArrayList();

		final Class<ActionNameSelector> actionNameSelectorClass = ActionNameSelector.class;
		for (MethodInfo methodInfo : aliasedMethods) {
			boolean isMatch = true;
			for (AnnotationInfo<ActionNameSelector> annotationInfo : methodInfo.getAnnotations(actionNameSelectorClass)) {
				if (!invokeActionNameSelector(annotationInfo, controllerContext, actionName, methodInfo))
					isMatch = false;
			}

			if (isMatch) {
				methods.add(methodInfo);
			}
		}

		return methods;
	}

	private static List<MethodInfo> runSelectionFilters(final ControllerContext controllerContext, final List<MethodInfo> methods) {
		// remove all methods which are opting out of this request
		// to opt out, at least one attribute defined on the method must return false

		final List<MethodInfo> matchesWithSelection = Lists.newArrayList();
		final List<MethodInfo> matchesWithoutSelection = Lists.newArrayList();

		for (MethodInfo method : methods) {
			final ImmutableCollection<AnnotationInfo<nextmethod.web.mvc.annotations.ActionMethodSelector>> a = method.getAnnotations(nextmethod.web.mvc.annotations.ActionMethodSelector.class);
			if (a.isEmpty()) {
				matchesWithoutSelection.add(method);
			} else {
				boolean isMatch = true;
				for (AnnotationInfo<nextmethod.web.mvc.annotations.ActionMethodSelector> annotationInfo : a) {
					if (!invokeActionMethodSelector(annotationInfo, controllerContext, method))
						isMatch = false;
				}
				if (isMatch)
					matchesWithSelection.add(method);
			}
		}

		// if a matching action method had a selection attribute, consider it more specific than a matching action method
		// without a selection attribute
		return !matchesWithSelection.isEmpty() ? matchesWithSelection : matchesWithoutSelection;
	}

	private static boolean invokeActionNameSelector(@Nonnull final AnnotationInfo<ActionNameSelector> annInfo, @Nonnull final ControllerContext controllerContext, @Nonnull final String actionName, @Nonnull final MethodInfo actionMethod) {
		final ActionNameSelector annotation = annInfo.wrappedType();
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

	private static boolean invokeActionMethodSelector(@Nonnull final AnnotationInfo<nextmethod.web.mvc.annotations.ActionMethodSelector> annInfo, @Nonnull final ControllerContext controllerContext, @Nonnull final MethodInfo actionMethod) {
		final nextmethod.web.mvc.annotations.ActionMethodSelector annotation = annInfo.wrappedType();
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

	private static final ClassInfo<Controller> ControllerClass = typeOf(Controller.class);

	private static Predicate<MethodInfo> createIsValidActionMethodPredicate() {
		return new Predicate<MethodInfo>() {
			@Override
			public boolean apply(@Nullable final MethodInfo input) {
				return input != null
					&& input.isPublic()
					&& input.getReflectedType().isA(ControllerClass);
			}
		};
	}

	private static Predicate<MethodInfo> createIsMethodDecoratedWithAliasingAnnotationPredicate() {
		return new Predicate<MethodInfo>() {
			@Override
			public boolean apply(@Nullable final MethodInfo input) {
				return input != null
					&& !input.getAnnotations(ActionNameSelector.class).isEmpty();
			}
		};
	}
}
