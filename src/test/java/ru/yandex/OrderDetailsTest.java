package ru.yandex;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assumptions;
import API.OrderAPI;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.*;

@DisplayName("Тесты детальной информации о заказах")
@Epic("API тесты для сервиса доставки")
@Feature("Детали заказов")
public class OrderDetailsTest extends BaseOrderTest {

    private int orderId;
    private int trackNumber;

    @BeforeEach
    @Step("Подготовка тестовых данных: создание заказа и получение orderId")
    public void setUpOrderDetails() {
        // Создаем заказ и получаем track number
        data.Order testOrder = createTestOrder();  // ✅ Явное указание пакета
        Response createResponse = OrderAPI.createOrder(testOrder);
        Assumptions.assumeTrue(OrderAPI.isOrderCreatedSuccessfully(createResponse),
                "Заказ не был создан для теста");

        trackNumber = OrderAPI.getTrackNumber(createResponse);

        // Получаем orderId по track number
        Response trackResponse = OrderAPI.getOrderByTrack(trackNumber);
        Assumptions.assumeTrue(OrderAPI.isOrderFoundByTrack(trackResponse),
                "Не удалось найти заказ по track number");

        orderId = OrderAPI.getOrderId(trackResponse);
    }

    @AfterEach
    @Step("Очистка тестовых данных: отмена заказа")
    public void tearDownOrder() {
        if (trackNumber != 0) {
            OrderAPI.cancelOrder(trackNumber);
        }
    }

    @Test
    @Story("Получение деталей заказа")
    @DisplayName("Успешное получение деталей заказа по ID")
    @Step("Получение деталей заказа по ID: {orderId}")
    public void getOrderDetailsByIdSuccess() {
        OrderAPI.getOrderById(orderId)
                .then()
                .statusCode(200)
                .body("order", notNullValue())
                .body("order.id", equalTo(orderId))
                .body("order.track", notNullValue())
                .body("order.firstName", notNullValue())
                .body("order.lastName", notNullValue());
    }

    @Test
    @Story("Получение деталей заказа")
    @DisplayName("Получение деталей несуществующего заказа")
    @Step("Попытка получения деталей несуществующего заказа (ID: 999999)")
    public void getOrderDetailsForNonExistentOrderFails() {
        OrderAPI.getOrderById(999999)
                .then()
                .statusCode(404)
                .body("message", notNullValue());
    }

    @Test
    @Story("Получение деталей заказа")
    @DisplayName("Получение деталей заказа с невалидным ID")
    @Step("Попытка получения деталей заказа с невалидным ID: 0")
    public void getOrderDetailsWithInvalidIdFails() {
        OrderAPI.getOrderById(0)
                .then()
                .statusCode(400)
                .body("message", notNullValue());
    }

    @Test
    @Story("Получение деталей заказа")
    @DisplayName("Получение деталей заказа по track number")
    @Step("Получение деталей заказа по track number")
    public void getOrderDetailsByTrackSuccess() {
        // Создаем еще один заказ специально для этого теста
        data.Order testOrder = createTestOrder();  // ✅ Явное указание пакета
        Response createResponse = OrderAPI.createOrder(testOrder);
        Assumptions.assumeTrue(OrderAPI.isOrderCreatedSuccessfully(createResponse),
                "Заказ не был создан для теста");

        int newTrackNumber = OrderAPI.getTrackNumber(createResponse);

        OrderAPI.getOrderByTrack(newTrackNumber)
                .then()
                .statusCode(200)
                .body("order", notNullValue())
                .body("order.track", equalTo(newTrackNumber));

        // Очищаем дополнительный заказ
        OrderAPI.cancelOrder(newTrackNumber);
    }

    // Вспомогательный метод для создания тестового заказа
    private data.Order createTestOrder() {  // ✅ Явное указание возвращаемого типа
        return new data.Order(  // ✅ Явное указание пакета при создании
                "Иван",
                "Петров",
                "ул. Ленина, д. 123",
                "4",
                "+79991234567",
                3,
                "2024-08-25",
                "Тестовый заказ",
                new String[]{"BLACK"}
        );
    }
}