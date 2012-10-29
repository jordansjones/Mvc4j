package nextmethod.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signifies that a public API (public class, method, or field) is not intended
 * for general consumption and is subject to incompatible changes, or even
 * removal, in a future release. This is due to a lack of more granular access
 * modifiers in the Java language.
 */
@Retention(RetentionPolicy.CLASS)
@Target({
	ElementType.ANNOTATION_TYPE,
	ElementType.CONSTRUCTOR,
	ElementType.FIELD,
	ElementType.METHOD,
	ElementType.TYPE
})
public @interface Internal {
}
