package ru.yandex;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;

public class OrderAPI {

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru";
    private static final String ORDER_PATH = "/api/v1/orders";

    // --- ВЛОЖЕННЫЕ DTO-КЛАССЫ ---

    public static class Order {
        private String firstName;
        private String lastName;
        private String address;
        private String metroStation;
        private String phone;
        private int rentTime;
        private String deliveryDate;
        private String comment;
        private String[] color;

        public Order() {
        }

        public Order(String firstName, String lastName, String address, String metroStation,
                     String phone, int rentTime, String deliveryDate, String comment, String[] color) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.address = address;
            this.metroStation = metroStation;
            this.phone = phone;
            this.rentTime = rentTime;
            this.deliveryDate = deliveryDate;
            this.comment = comment;
            this.color = color;
        }

        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getAddress() { return address; }
        public String getMetroStation() { return metroStation; }
        public String getPhone() { return phone; }
        public int getRentTime() { return rentTime; }
        public String getDeliveryDate() { return deliveryDate; }
        public String getComment() { return comment; }
        public String[] getColor() { return color; }

        public void setFirstName(String firstName) { this.firstName = firstName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public void setAddress(String address) { this.address = address; }
        public void setMetroStation(String metroStation) { this.metroStation = metroStation; }
        public void setPhone(String phone) { this.phone = phone; }
        public void setRentTime(int rentTime) { this.rentTime = rentTime; }
        public void setDeliveryDate(String deliveryDate) { this.deliveryDate = deliveryDate; }
        public void setComment(String comment) { this.comment = comment; }
        public void setColor(String[] color) { this.color = color; }
    }

    public static class CancelOrderRequest {
        private int track;

        public CancelOrderRequest(int track) {
            this.track = track;
        }

        public int getTrack() { return track; }
    }

    public static class AcceptOrderRequest {
        private int courierId;

        public AcceptOrderRequest(int courierId) {
            this.courierId = courierId;
        }

        public int getCourierId() { return courierId; }
    }

    // --- МЕТОДЫ API ---

    @Step("Создание заказа")
    public static Response createOrder(Order order) {
        return given()
                .contentType("application/json")
                .body(order)
                .when()
                .post(BASE_URI + ORDER_PATH);
    }

    @Step("Получение списка заказов")
    public static Response getOrdersList() {
        return given()
                .when()
                .get(BASE_URI + ORDER_PATH);
    }

    @Step("Получение заказа по ID: {orderId}")
    public static Response getOrderById(int orderId) {
        return given()
                .when()
                .get(BASE_URI + ORDER_PATH + "/" + orderId);
    }

    @Step("Получение заказа по track номеру: {trackNumber}")
    public static Response getOrderByTrack(int trackNumber) {
        return given()
                .queryParam("t", trackNumber)
                .when()
                .get(BASE_URI + ORDER_PATH + "/track");
    }

    @Step("Отмена заказа с track номером: {trackNumber}")
    public static Response cancelOrder(int trackNumber) {
        return given()
                .contentType("application/json")
                .body(new CancelOrderRequest(trackNumber))
                .when()
                .put(BASE_URI + ORDER_PATH + "/cancel");
    }

    @Step("Принятие заказа {orderId} курьером {courierId}")
    public static Response acceptOrder(int orderId, int courierId) {
        return given()
                .contentType("application/json")
                .body(new AcceptOrderRequest(courierId))
                .when()
                .put(BASE_URI + ORDER_PATH + "/accept/" + orderId);
    }

    @Step("Завершение заказа: {orderId}")
    public static Response finishOrder(int orderId) {
        return given()
                .when()
                .put(BASE_URI + ORDER_PATH + "/finish/" + orderId);
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