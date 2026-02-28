package validationsFramework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Range {
    int max() default Integer.MAX_VALUE;
    int min() default Integer.MIN_VALUE;

    String message() default "Number is not in range";
}
