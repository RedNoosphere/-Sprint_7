package ru.yandex;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.Matchers.*;

@DisplayName("Тесты создания заказов")
@Epic("API тесты для сервиса доставки")
@Feature("Создание заказов")
public class OrderCreationTest extends BaseOrderTest {

    @Test
    @Story("Позитивные сценарии создания")
    @DisplayName("Успешное создание заказа с черным самокатом")
    @Step("Создание заказа с цветом BLACK")
    public void createOrderWithBlackScooterSuccess() {
        OrderAPI.Order order = createOrderWithColor(new String[]{"BLACK"});

        OrderAPI.createOrder(order)
                .then()
                .statusCode(201)
                .body("track", notNullValue());
    }

    @Test
    @Story("Позитивные сценарии создания")
    @DisplayName("Успешное создание заказа с серым самокатом")
    @Step("Создание заказа с цветом GREY")
    public void createOrderWithGreyScooterSuccess() {
        OrderAPI.Order order = createOrderWithColor(new String[]{"GREY"});

        OrderAPI.createOrder(order)
                .then()
                .statusCode(201)
                .body("track", notNullValue());
    }

    @Test
    @Story("Позитивные сценарии создания")
    @DisplayName("Успешное создание заказа с двумя цветами")
    @Step("Создание заказа с цветами BLACK и GREY")
    public void createOrderWithBothColorsSuccess() {
        OrderAPI.Order order = createOrderWithColor(new String[]{"BLACK", "GREY"});

        OrderAPI.createOrder(order)
                .then()
                .statusCode(201)
                .body("track", notNullValue());
    }

    @Test
    @Story("Позитивные сценарии создания")
    @DisplayName("Успешное создание заказа без указания цвета")
    @Step("Создание заказа без указания цвета")
    public void createOrderWithoutColorSuccess() {
        OrderAPI.Order order = createOrderWithColor(new String[]{});

        OrderAPI.createOrder(order)
                .then()
                .statusCode(201)
                .body("track", notNullValue());
    }

    @Test
    @Story("Негативные сценарии создания")
    @DisplayName("Создание заказа без обязательного поля (firstName)")
    @Step("Попытка создания заказа без firstName")
    public void createOrderWithoutFirstNameFails() {
        OrderAPI.Order invalidOrder = OrderAPI.createCustomOrder(
                null, // firstName - обязательное поле
                "Петров", "ул. Ленина, д. 123", "4",
                "+79991234567", 3, "2024-08-25",
                "Тестовый заказ", new String[]{"BLACK"}
        );

        OrderAPI.createOrder(invalidOrder)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @Story("Негативные сценарии создания")
    @DisplayName("Создание заказа без обязательного поля (lastName)")
    @Step("Попытка создания заказа без lastName")
    public void createOrderWithoutLastNameFails() {
        OrderAPI.Order invalidOrder = OrderAPI.createCustomOrder(
                "Иван", null, "ул. Ленина, д. 123", "4",
                "+79991234567", 3, "2024-08-25",
                "Тестовый заказ", new String[]{"BLACK"}
        );

        OrderAPI.createOrder(invalidOrder)
                .then()
                .statusCode(400);
    }

    // УДАЛЕНО: повторное объявление метода createOrderWithColor
    // Метод уже наследуется из BaseOrderTest с модификатором protected
}