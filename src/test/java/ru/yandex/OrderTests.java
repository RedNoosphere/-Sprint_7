package ru.yandex;

import API.CourierAPI;
import API.OrderAPI;
import data.Courier;
import data.CourierCredentials;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assumptions;

import static org.hamcrest.Matchers.*;

@DisplayName("Тесты операций с заказами")
@Epic("API тесты для сервиса доставки")
@Feature("Операции с заказами")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderTests extends BaseOrderTest {

    private int courierId;
    private static String testCourierLogin;
    private static String testCourierPassword = "1234";

    @BeforeAll
    @Step("Создание тестового курьера (BeforeAll)")
    public static void setUpTestCourier() {
        // Создаем тестового курьера
        testCourierLogin = "testCourier_" + System.currentTimeMillis();
        Courier testCourier = new Courier(testCourierLogin, testCourierPassword, "Test Courier");

        // Регистрируем курьера
        Response createResponse = CourierAPI.createCourier(testCourier);
        Assumptions.assumeTrue(CourierAPI.isCourierCreatedSuccessfully(createResponse),
                "Не удалось создать тестового курьера");
    }

    @BeforeEach
    @Step("Подготовка данных курьера (BeforeEach)")
    public void setUpCourier() {
        // Получаем courierId через API класс
        try {
            Integer courierIdResult = CourierAPI.getCourierId(testCourierLogin, testCourierPassword);
            if (courierIdResult != null) {
                this.courierId = courierIdResult;
            } else {
                this.courierId = 0;
            }
        } catch (Exception e) {
            this.courierId = 0;
        }
    }

    @AfterAll
    @Step("Удаление тестового курьера (AfterAll)")
    public static void tearDownTestCourier() {
        // Удаляем тестового курьера
        if (testCourierLogin != null) {
            CourierAPI.deleteCourierIfExists(testCourierLogin, testCourierPassword);
        }
    }

    @Test
    @org.junit.jupiter.api.Order(1)  // ✅ Явное указание пакета для аннотации
    @Story("Отмена заказов")
    @DisplayName("Успешная отмена заказа")
    @Step("Отмена заказа с track номером: {trackNumber}")
    public void cancelOrderSuccess() {
        Assumptions.assumeTrue(trackNumber != 0, "Заказ не был создан для теста");

        OrderAPI.cancelOrder(trackNumber)
                .then()
                .statusCode(200)
                .body("ok", equalTo(true));
    }

    @Test
    @org.junit.jupiter.api.Order(2)  // ✅ Явное указание пакета для аннотации
    @Story("Отмена заказов")
    @DisplayName("Отмена несуществующего заказа")
    @Step("Попытка отмены несуществующего заказа (track: 999999)")
    public void cancelNonExistentOrderFails() {
        OrderAPI.cancelOrder(999999)
                .then()
                .statusCode(404);
    }

    @Test
    @org.junit.jupiter.api.Order(3)  // ✅ Явное указание пакета для аннотации
    @Story("Принятие заказов")
    @DisplayName("Принятие заказа курьером")
    @Step("Принятие заказа курьером {courierId}")
    public void acceptOrderSuccess() {
        Assumptions.assumeTrue(courierId != 0, "Courier ID не был получен");

        // Создаем отдельный заказ для этого теста
        data.Order order = new data.Order(  // ✅ Явное указание пакета
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

        Response createResponse = OrderAPI.createOrder(order);
        Assumptions.assumeTrue(OrderAPI.isOrderCreatedSuccessfully(createResponse),
                "Не удалось создать заказ для теста принятия");

        int testTrackNumber = OrderAPI.getTrackNumber(createResponse);
        Response trackResponse = OrderAPI.getOrderByTrack(testTrackNumber);
        Assumptions.assumeTrue(OrderAPI.isOrderFoundByTrack(trackResponse),
                "Не удалось найти заказ по track number");

        int testOrderId = OrderAPI.getOrderId(trackResponse);

        OrderAPI.acceptOrder(testOrderId, courierId)
                .then()
                .statusCode(200)
                .body("ok", equalTo(true));

        // Очищаем тестовый заказ
        OrderAPI.cancelOrder(testTrackNumber);
    }

    @Test
    @org.junit.jupiter.api.Order(4)  // ✅ Явное указание пакета для аннотации
    @Story("Завершение заказов")
    @DisplayName("Завершение заказа")
    @Step("Завершение заказа курьером {courierId}")
    public void finishOrderSuccess() {
        Assumptions.assumeTrue(courierId != 0, "Courier ID не был получен");

        // Создаем и принимаем заказ для завершения
        data.Order order = new data.Order(  // ✅ Явное указание пакета
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

        Response createResponse = OrderAPI.createOrder(order);
        Assumptions.assumeTrue(OrderAPI.isOrderCreatedSuccessfully(createResponse),
                "Не удалось создать заказ для теста завершения");

        int testTrackNumber = OrderAPI.getTrackNumber(createResponse);
        Response trackResponse = OrderAPI.getOrderByTrack(testTrackNumber);
        Assumptions.assumeTrue(OrderAPI.isOrderFoundByTrack(trackResponse),
                "Не удалось найти заказ по track number");

        int testOrderId = OrderAPI.getOrderId(trackResponse);

        // Принимаем заказ
        OrderAPI.acceptOrder(testOrderId, courierId);

        // Завершаем заказ
        OrderAPI.finishOrder(testOrderId)
                .then()
                .statusCode(200)
                .body("ok", equalTo(true));

        // Очищаем тестовый заказ
        OrderAPI.cancelOrder(testTrackNumber);
    }
}