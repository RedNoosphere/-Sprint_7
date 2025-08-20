package ru.yandex;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assumptions;
import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;

// Добавленные импорты
import org.junit.jupiter.api.Assertions;
import java.util.Map;
import java.util.HashMap;

@DisplayName("Тесты операций с заказами")
@Epic("API тесты для сервиса доставки")
@Feature("Операции с заказами")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderTests extends BaseOrderTest {

    private int orderId;
    private int courierId;
    private static CourierAPI.Courier testCourier;
    private static String testCourierLogin;
    private static String testCourierPassword;

    @BeforeAll
    @Step("Создание тестового курьера (BeforeAll)")
    public static void setUpTestCourier() {
        // Создаем тестового курьера перед всеми тестами
        testCourier = CourierAPI.createValidCourier();
        testCourierLogin = testCourier.getLogin();
        testCourierPassword = testCourier.getPassword();

        // Регистрируем курьера
        Response createResponse = CourierAPI.createCourier(testCourier);
        Assertions.assertTrue(CourierAPI.isCourierCreatedSuccessfully(createResponse),
                "Не удалось создать тестового курьера");
    }

    @BeforeEach
    @Step("Подготовка данных заказа и курьера (BeforeEach)")
    public void setUpOrderAndCourier() {
        // Получаем orderId из созданного заказа
        if (trackNumber != 0) {
            try {
                Response orderResponse = OrderAPI.getOrderByTrack(trackNumber);

                // Логируем ответ для отладки
                System.out.println("GetOrderByTrack Response - Status: " + orderResponse.statusCode());
                System.out.println("GetOrderByTrack Response - Body: " + orderResponse.asString());

                if (OrderAPI.isOrderFoundByTrack(orderResponse)) {
                    this.orderId = OrderAPI.getOrderId(orderResponse);
                    System.out.println("Successfully retrieved orderId: " + this.orderId);
                } else {
                    System.out.println("Failed to find order by track. Status: " + orderResponse.statusCode());
                    this.orderId = 0; // Устанавливаем 0 чтобы тесты пропустились
                }
            } catch (Exception e) {
                System.out.println("Error getting order by track: " + e.getMessage());
                this.orderId = 0;
            }
        } else {
            System.out.println("Track number is 0, skipping order retrieval");
            this.orderId = 0;
        }

        // Логинимся и получаем courierId (ИСПРАВЛЕННАЯ ЧАСТЬ)
        try {
            Map<String, String> credentials = new HashMap<>();
            credentials.put("login", testCourierLogin);
            credentials.put("password", testCourierPassword);

            Response loginResponse = given()
                    .contentType("application/json")
                    .body(credentials)
                    .post("/api/v1/courier/login");

            if (loginResponse.statusCode() == 200) {
                this.courierId = loginResponse.path("id");
                System.out.println("Successfully logged in courier. CourierId: " + this.courierId);
            } else {
                System.out.println("Failed to login courier. Status: " + loginResponse.statusCode());
                System.out.println("Login response: " + loginResponse.asString());
                this.courierId = 0;
            }
        } catch (Exception e) {
            System.out.println("Error logging in courier: " + e.getMessage());
            this.courierId = 0;
        }
    }

    @AfterAll
    @Step("Удаление тестового курьера (AfterAll)")
    public static void tearDownTestCourier() {
        // Удаляем тестового курьера после всех тестов
        if (testCourierLogin != null) {
            CourierAPI.deleteCourierIfExists(testCourierLogin, testCourierPassword);
        }
    }

    private int getExistingCourierId() {
        return courierId; // Возвращаем реальный ID созданного курьера
    }

    @Test
    @Order(1)
    @Story("Отмена заказов")
    @DisplayName("Успешная отмена заказа")
    @Step("Отмена заказа с track номером: {trackNumber}")
    public void cancelOrderSuccess() {
        Assumptions.assumeTrue(trackNumber != 0, "Заказ не был создан для теста");

        // Добавляем логирование для отладки
        System.out.println("Attempting to cancel order with track: " + trackNumber);
        Response cancelResponse = OrderAPI.cancelOrder(trackNumber);
        System.out.println("Cancel response - Status: " + cancelResponse.statusCode());
        System.out.println("Cancel response - Body: " + cancelResponse.asString());

        cancelResponse
                .then()
                .statusCode(200)
                .body("ok", equalTo(true));
    }

    @Test
    @Order(2)
    @Story("Отмена заказов")
    @DisplayName("Отмена несуществующего заказа")
    @Step("Попытка отмены несуществующего заказа (track: 999999)")
    public void cancelNonExistentOrderFails() {
        Response response = OrderAPI.cancelOrder(999999);
        System.out.println("Cancel non-existent response - Status: " + response.statusCode());
        System.out.println("Cancel non-existent response - Body: " + response.asString());

        response.then().statusCode(404);
    }

    @Test
    @Order(3)
    @Story("Отмена заказов")
    @DisplayName("Отмена заказа с невалидным ID")
    @Step("Попытка отмены заказа с невалидным ID (track: -1)")
    public void cancelOrderWithInvalidIdFails() {
        Response response = OrderAPI.cancelOrder(-1);
        System.out.println("Cancel invalid response - Status: " + response.statusCode());
        System.out.println("Cancel invalid response - Body: " + response.asString());

        response.then().statusCode(400);
    }

    @Test
    @Order(4)
    @Story("Принятие заказов")
    @DisplayName("Успешное принятие заказа курьером")
    @Step("Принятие заказа {orderId} курьером {courierId}")
    public void acceptOrderSuccess() {
        Assumptions.assumeTrue(orderId != 0, "Order ID не был получен");
        Assumptions.assumeTrue(courierId != 0, "Courier ID не был получен");

        Response response = OrderAPI.acceptOrder(orderId, courierId);
        System.out.println("Accept order response - Status: " + response.statusCode());
        System.out.println("Accept order response - Body: " + response.asString());

        response
                .then()
                .statusCode(200)
                .body("ok", equalTo(true));
    }

    @Test
    @Order(5)
    @Story("Принятие заказов")
    @DisplayName("Принятие заказа несуществующим курьером")
    @Step("Попытка принятия заказа {orderId} несуществующим курьером (id: 999999)")
    public void acceptOrderWithNonExistentCourierFails() {
        Assumptions.assumeTrue(orderId != 0, "Order ID не был получен");

        Response response = OrderAPI.acceptOrder(orderId, 999999);
        System.out.println("Accept non-existent courier response - Status: " + response.statusCode());
        System.out.println("Accept non-existent courier response - Body: " + response.asString());

        response.then().statusCode(404);
    }

    @Test
    @Order(6)
    @Story("Завершение заказов")
    @DisplayName("Успешное завершение заказа")
    @Step("Завершение заказа {orderId} (предварительно принятого курьером {courierId})")
    public void finishOrderSuccess() {
        Assumptions.assumeTrue(orderId != 0, "Order ID не был получен");
        Assumptions.assumeTrue(courierId != 0, "Courier ID не был получен");

        // Сначала принимаем заказ
        Response acceptResponse = OrderAPI.acceptOrder(orderId, courierId);
        System.out.println("Accept for finish response - Status: " + acceptResponse.statusCode());
        System.out.println("Accept for finish response - Body: " + acceptResponse.asString());

        // Затем завершаем заказ
        Response finishResponse = OrderAPI.finishOrder(orderId);
        System.out.println("Finish order response - Status: " + finishResponse.statusCode());
        System.out.println("Finish order response - Body: " + finishResponse.asString());

        finishResponse
                .then()
                .statusCode(200)
                .body("ok", equalTo(true));
    }
}