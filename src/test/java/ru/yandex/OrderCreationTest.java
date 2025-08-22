package ru.yandex;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;

@DisplayName("Тесты создания заказов")
@Epic("API тесты для сервиса доставки")
@Feature("Создание заказов")
public class OrderCreationTest extends BaseOrderTest {

    // Параметризованный тест для разных цветов
    @ParameterizedTest(name = "Создание заказа с цветами: {0}")
    @MethodSource("colorProvider")
    @Story("Позитивные сценарии создания")
    @DisplayName("Успешное создание заказа с разными цветами")
    @Step("Создание заказа с цветами: {arguments}")
    public void createOrderWithDifferentColorsSuccess(String testName, String[] colors) {
        OrderAPI.Order order = createOrderWithColor(colors);

        OrderAPI.createOrder(order)
                .then()
                .statusCode(201)
                .body("track", notNullValue());
    }

    // Провайдер данных для цветов
    private static Stream<Arguments> colorProvider() {
        return Stream.of(
                Arguments.of("Черный самокат", new String[]{"BLACK"}),
                Arguments.of("Серый самокат", new String[]{"GREY"}),
                Arguments.of("Оба цвета", new String[]{"BLACK", "GREY"}),
                Arguments.of("Без цвета", new String[]{})
        );
    }

    // Параметризованный тест для обязательных полей
    @ParameterizedTest(name = "Создание заказа без обязательного поля: {0}")
    @MethodSource("requiredFieldProvider")
    @Story("Негативные сценарии создания")
    @DisplayName("Создание заказа без обязательных полей")
    @Step("Попытка создания заказа без {0}")
    public void createOrderWithoutRequiredFieldFails(String fieldName, OrderAPI.Order invalidOrder) {
        OrderAPI.createOrder(invalidOrder)
                .then()
                .statusCode(400);
    }

    // Провайдер данных для обязательных полей
    private static Stream<Arguments> requiredFieldProvider() {
        return Stream.of(
                Arguments.of("firstName", OrderAPI.createCustomOrder(
                        null, "Петров", "ул. Ленина, д. 123", "4",
                        "+79991234567", 3, "2024-08-25",
                        "Тестовый заказ", new String[]{"BLACK"}
                )),
                Arguments.of("lastName", OrderAPI.createCustomOrder(
                        "Иван", null, "ул. Ленина, д. 123", "4",
                        "+79991234567", 3, "2024-08-25",
                        "Тестовый заказ", new String[]{"BLACK"}
                ))
        );
    }

    // Дополнительные тесты для граничных случаев
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    @Story("Валидация данных")
    @DisplayName("Создание заказа с пустыми логинами")
    @Step("Попытка создания заказа с пустым логином: '{login}'")
    public void createOrderWithEmptyLoginFails(String login) {
        OrderAPI.Order invalidOrder = OrderAPI.createCustomOrder(
                login, "Петров", "ул. Ленина, д. 123", "4",
                "+79991234567", 3, "2024-08-25",
                "Тестовый заказ", new String[]{"BLACK"}
        );

        OrderAPI.createOrder(invalidOrder)
                .then()
                .statusCode(400);
    }
}