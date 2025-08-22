package ru.yandex;

import io.qameta.allure.*;
import io.restassured.response.Response; // Добавляем этот импорт
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assumptions;
import ru.yandex.BaseOrderTest;
import ru.yandex.OrderAPI;

import static org.hamcrest.Matchers.*;

@DisplayName("Тесты детальной информации о заказах")
@Epic("API тесты для сервиса доставки")
@Feature("Детали заказов")
public class OrderDetailsTest extends BaseOrderTest {

    @Test
    @Story("Получение деталей заказа")
    @DisplayName("Успешное получение деталей заказа по ID")
    @Step("Получение деталей заказа по ID: {trackNumber}")
    public void getOrderDetailsByIdSuccess() {
        Assumptions.assumeTrue(trackNumber != 0, "Заказ не был создан для теста");

        // Предполагая, что trackNumber можно использовать как orderId
        // Или нужна дополнительная логика для получения orderId
        OrderAPI.getOrderById(trackNumber)
                .then()
                .statusCode(200)
                .body("order", notNullValue())
                .body("order.id", notNullValue());
    }

    @Test
    @Story("Получение деталей заказа")
    @DisplayName("Получение деталей несуществующего заказа")
    @Step("Попытка получения деталей несуществующего заказа (ID: 999999)")
    public void getOrderDetailsForNonExistentOrderFails() {
        OrderAPI.getOrderById(999999)
                .then()
                .statusCode(404);
    }

    @Test
    @Story("Получение деталей заказа")
    @DisplayName("Получение деталей заказа с невалидным ID")
    @Step("Попытка получения деталей заказа с невалидным ID: -1")
    public void getOrderDetailsWithInvalidIdFails() {
        OrderAPI.getOrderById(-1)
                .then()
                .statusCode(400);
    }

    @Step("Вызов API для получения заказа по ID: {orderId}")
    private static Response getOrderById(int orderId) {
        return OrderAPI.getOrderById(orderId);
    }
}