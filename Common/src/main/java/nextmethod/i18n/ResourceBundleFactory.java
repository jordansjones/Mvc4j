package nextmethod.i18n;

import com.google.common.base.Strings;
import nextmethod.i18n.annotations.Bundle;
import nextmethod.i18n.annotations.DefaultMessage;
import nextmethod.i18n.annotations.Key;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import static com.google.common.base.Preconditions.checkArgument;

// TODO: Look for runtime optimizations (Caching?)
public final class ResourceBundleFactory<T extends IResourceBundle> implements InvocationHandler {

	private final Class<T> resourceClass;
	private final ResourceBundle resourceBundle;

	private ResourceBundleFactory(final Class<T> resourceClass) {
		this.resourceClass = resourceClass;
		this.resourceBundle = ResourceBundle.getBundle(getBundleValue(resourceClass), new XmlResourceBundleControl());
	}

	private String getMessageTemplate(final Method method) {
		final DefaultMessage defaultMessage = method.getAnnotation(DefaultMessage.class);
		if (defaultMessage != null && !Strings.isNullOrEmpty(defaultMessage.value())) {
			return defaultMessage.value();
		}
		final String key = method.getAnnotation(Key.class).value();
		return this.resourceBundle.getString(key);
	}

	private String formatMessageTemplate(final String template, final Object[] args) {
		return MessageFormat.format(template, args);
	}

	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
		checkValidMethodAnnotation(method, this.resourceClass);
		final String template = getMessageTemplate(method);
		if (args == null || args.length < 1) {
			return template;
		}
		return formatMessageTemplate(template, args);
	}

	private static String getBundleValue(final Class<? extends IResourceBundle> resourceClass) {
		return resourceClass.getAnnotation(Bundle.class).value();
	}

	public static <RType extends IResourceBundle> RType newInstance(final Class<RType> cls) {
		checkValidBundleAnnotation(cls);

		final Object o = Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls}, new ResourceBundleFactory<RType>(cls));
		return cls.cast(o);
	}

	private static void checkValidMethodAnnotation(final Method method, final Class<?> cls) {
		final Key annotation = method.getAnnotation(Key.class);
		checkArgument(annotation != null, "%s.%s must be annotated with %s", cls.getName(), method.getName(), Key.class.getName());
		assert annotation != null;
		final String value = annotation.value();
		checkArgument(!Strings.isNullOrEmpty(value), "%s.%s %s annotation must have a valid value", cls.getName(), method.getName(), Key.class.getName());
	}

	private static void checkValidBundleAnnotation(final Class<? extends IResourceBundle> cls) {
		final Bundle annotation = cls.getAnnotation(Bundle.class);
		checkArgument(annotation != null, "%s must be annotated with %s", cls.getName(), Bundle.class.getName());
		assert annotation != null;
		final String bundleName = annotation.value();
		checkArgument(!Strings.isNullOrEmpty(bundleName), "%s %s annotation must have a valid value", cls.getName(), Bundle.class.getName());
	}
}
