package nextmethod.i18n.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Default text to be used if no translation is found (and also used as the
 * source for translation). Format should be that expected by
 * {@link java.text.MessageFormat}.
 * <p/>
 * <p>Example:
 * <code><pre>
 *   &#64;DefaultMessage("Don''t panic - you have {0} widgets left")
 *   String example(int count)
 * </pre></code>
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface DefaultMessage {
	String value();
}
