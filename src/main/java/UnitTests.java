import org.junit.jupiter.api.Test;
import validationsFramework.ValidationResult;
import validationsFramework.Validator;
import validationsFramework.annotations.Email;
import validationsFramework.annotations.NotNull;
import validationsFramework.annotations.Range;
import validationsFramework.annotations.Size;

import static org.junit.jupiter.api.Assertions.*;

class User {
    @NotNull(message = "Имя не может быть null")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    private String name;

    @Email(message = "Некорректный формат email")
    @NotNull(message = "Email не может быть null")
    private String email;

    @Range(min = 0, max = 150, message = "Возраст должен быть от 0 до 150")
    private Integer age;

    @Size(min = 6, max = 20, message = "Пароль должен быть от 6 до 20 символов")
    private String password;

    // Конструкторы, геттеры и сеттеры
    public User() {}

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setAge(Integer age) { this.age = age; }
    public void setPassword(String password) { this.password = password; }
}

class UnitTests {

    @Test
    void testValidObject() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setAge(25);
        user.setPassword("securepassword");

        ValidationResult result = Validator.validate(user);

        assertTrue(result.isValid());
        assertEquals(0, result.getErrors().size());
    }

    @Test
    void testNotNullValidation() {
        User user = new User();
        user.setName(null); // Должно вызвать ошибку @NotNull
        user.setEmail("test@example.com");
        user.setAge(25);

        ValidationResult result = Validator.validate(user);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.contains("Имя не может быть null")));
    }

    @Test
    void testSizeValidation() {
        User user = new User();
        user.setName("A"); // Слишком короткое имя
        user.setEmail("test@example.com");
        user.setAge(25);
        user.setPassword("123"); // Слишком короткий пароль

        ValidationResult result = Validator.validate(user);

        assertFalse(result.isValid());
        assertEquals(2, result.getErrors().size());
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.contains("Имя должно быть от 2 до 50 символов")));
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.contains("Пароль должен быть от 6 до 20 символов")));
    }

    @Test
    void testRangeValidation() {
        User user = new User();
        user.setName("John");
        user.setEmail("test@example.com");
        user.setAge(200); // Возраст вне диапазона
        user.setPassword("password");

        ValidationResult result = Validator.validate(user);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.contains("Возраст должен быть от 0 до 150")));
    }

    @Test
    void testEmailValidation() {
        User user = new User();
        user.setName("John");
        user.setEmail("invalid-email"); // Невалидный email
        user.setAge(25);

        ValidationResult result = Validator.validate(user);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.contains("Некорректный формат email")));
    }

    @Test
    void testMultipleValidationsOnSameField() {
        User user = new User();
        user.setName(null); // @NotNull сработает
        user.setEmail(null); // @NotNull и @Email сработают
        user.setAge(-5); // @Range сработает

        ValidationResult result = Validator.validate(user);

        assertFalse(result.isValid());
        // Должно быть минимум 3 ошибки
        assertTrue(result.getErrors().size() >= 3);
    }

    @Test
    void testBoundaryValues() {
        User user = new User();
        user.setName("Jo"); // Минимальная допустимая длина
        user.setEmail("a@b.co"); // Минимальный валидный email
        user.setAge(0); // Минимальный допустимый возраст
        user.setPassword("123456"); // Минимальная допустимая длина пароля

        ValidationResult result = Validator.validate(user);

        assertTrue(result.isValid());
    }
}