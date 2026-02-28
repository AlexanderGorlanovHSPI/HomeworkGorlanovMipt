package validationsFramework;

import validationsFramework.annotations.NotNull;
import validationsFramework.annotations.Size;
import validationsFramework.annotations.Range;
import validationsFramework.annotations.Email;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.regex.Pattern;

public class Validator {
    private static final Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public static ValidationResult validate(Object object) {
        ValidationResult result = new ValidationResult();

        if (object == null){
            result.addError("validated object cannot be null");
            return result;
        }

        Class<?> clas = object.getClass();
        Field[] fields = clas.getDeclaredFields();

        for (Field field : fields){
            field.setAccessible(true);

            if (field.isAnnotationPresent(NotNull.class)){
                validateNotNull(field, object, result);
            }

            if (field.isAnnotationPresent(Size.class)){
                validateSize(field, object, result);
            }

            if (field.isAnnotationPresent(Range.class)){
                validateRange(field, object, result);
            }

            if (field.isAnnotationPresent(Email.class)){
                validateEmail(field, object, result);
            }
        }

        return result;
    }

    private static void validateEmail(Field field, Object object, ValidationResult result) {
        try {
            Object value = field.get(object);

            if (value == null) {
                return;
            }

            if (value instanceof Number) {
                String email = (String) value;
                Email annotation = field.getAnnotation(Email.class);

                if (!emailPattern.matcher(email).matches()) {
                    result.addError(annotation.message());
                }
            }
        } catch (IllegalAccessException e){
            result.addError("Cannot access field: " + field.getName());
        }
    }

    private static void validateRange(Field field, Object object, ValidationResult result) {
        try {
            Object value = field.get(object);

            if (value == null) {
                return;
            }

            if (value instanceof Number) {
                Number numberValue = (Number) value;
                long longValue = numberValue.longValue();
                Range annotaion = field.getAnnotation(Range.class);

                if (longValue < annotaion.min() || longValue > annotaion.max()) {
                    result.addError(annotaion.message());
                }
            }
        } catch (IllegalAccessException e){
            result.addError("Cannot access field: " + field.getName());
        }
    }

    private static void validateSize(Field field, Object object, ValidationResult result) {
        try {
            Object value = field.get(object);

            if (value == null) {
                return;
            }

            if (value instanceof String){
                String stringValue = (String) value;
                Size annotaion = field.getAnnotation(Size.class);
                int len = stringValue.length();

                if (len < annotaion.min() || len > annotaion.max()){
                    result.addError(annotaion.message() + " (current: " + len + ", expected: " + annotaion.min() + "-" + annotaion.max() + ")");
                }
            }
        } catch (IllegalAccessException e){
            result.addError("Cannot access field: " + field.getName());
        }
    }

    private static void validateNotNull(Field field, Object object, ValidationResult result) {
        try {
            Object value = field.get(object);

            if (value == null){
                NotNull annotation = field.getAnnotation(NotNull.class);
                result.addError(annotation.message());
            }
        } catch (IllegalAccessException e){
            result.addError("Cannon access field: " + field.getName());
        }
    }
}
