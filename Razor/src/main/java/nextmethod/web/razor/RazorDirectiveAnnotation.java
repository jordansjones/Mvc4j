package nextmethod.web.razor;

import nextmethod.base.Strings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RazorDirectiveAnnotation {

	String name() default Strings.Empty;
	String value() default Strings.Empty;
}
