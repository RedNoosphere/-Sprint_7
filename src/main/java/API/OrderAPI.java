package API;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import data.Order;
import data.CancelOrderRequest;
import data.AcceptOrderRequest;

import static io.restassured.RestAssured.given;

public class OrderAPI {

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru";
    private static final String ORDER_PATH = "/api/v1/orders";

    static {
        RestAssured.baseURI = BASE_URI;
    }

    // --- МЕТОДЫ API ---

    @Step("Создание заказа")
    public static Response createOrder(Order order) {
        return given()
                .contentType("application/json")
                .body(order)
                .when()
                .post(ORDER_PATH);
    }

    @Step("Получение списка заказов")
    public static Response getOrdersList() {
        return given()
                .when()
                .get(ORDER_PATH);
    }

    @Step("Получение заказа по ID: {orderId}")
    public static Response getOrderById(int orderId) {
        return given()
                .when()
                .get(ORDER_PATH + "/" + orderId);
    }

    @Step("Получение заказа по track номеру: {trackNumber}")
    public static Response getOrderByTrack(int trackNumber) {
        return given()
                .queryParam("t", trackNumber)  // ✅ ПРАВИЛЬНО: параметр "t" согласно документации
                .when()
                .get(ORDER_PATH + "/track");
    }

    @Step("Отмена заказа с track номером: {trackNumber}")
    public static Response cancelOrder(int trackNumber) {
        CancelOrderRequest request = new CancelOrderRequest(trackNumber);
        return given()
                .contentType("application/json")
                .body(request)
                .when()
                .put(ORDER_PATH + "/cancel");
    }

    @Step("Принятие заказа {orderId} курьером {courierId}")
    public static Response acceptOrder(int orderId, int courierId) {
        AcceptOrderRequest request = new AcceptOrderRequest(courierId);
        return given()
                .contentType("application/json")
                .body(request)
                .when()
                .put(ORDER_PATH + "/accept/" + orderId);
    }

    @Step("Завершение заказа: {orderId}")
    public static Response finishOrder(int orderId) {
        return given()
                .when()
                .put(ORDER_PATH + "/finish/" + orderId);
    }

    // --- ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ---

    @Step("Создание валидного заказа с цветами: {colors}")
    public static Order createValidOrder(String[] colors) {
        return new Order(
                "Иван", "Петров", "ул. Ленина, д. 123", "4",
                "+79991234567", 3, "2024-08-25", "Тестовый заказ", colors
        );
    }

    @Step("Создание кастомного заказа")
    public static Order createCustomOrder(String firstName, String lastName, String address,
                                          String metroStation, String phone, int rentTime,
                                          String deliveryDate, String comment, String[] color) {
        return new Order(firstName, lastName, address, metroStation, phone,
                rentTime, deliveryDate, comment, color);
    }

    @Step("Проверка успешного создания заказа")
    public static boolean isOrderCreatedSuccessfully(Response response) {
        return response.statusCode() == 201 && response.path("track") != null;
    }

    @Step("Получение track номера из ответа")
    public static int getTrackNumber(Response response) {
        return response.path("track");
    }

    @Step("Получение orderId из ответа")
    public static int getOrderId(Response response) {
        if (response.statusCode() != 200) {
            throw new RuntimeException("Не удалось получить orderId. Status: " + response.statusCode());
        }

        Integer orderId = response.path("order.id");
        if (orderId == null) {
            throw new RuntimeException("Order ID не найден в ответе. Response: " + response.asString());
        }

        return orderId;
    }

    @Step("Проверка успешного получения заказа по track")
    public static boolean isOrderFoundByTrack(Response response) {
        return response.statusCode() == 200 && response.path("order") != null;
    }

    @Step("Проверка успешной отмены заказа")
    public static boolean isOrderCancelledSuccessfully(Response response) {
        return response.statusCode() == 200 && response.path("ok") != null && response.path("ok").equals(true);
    }

}