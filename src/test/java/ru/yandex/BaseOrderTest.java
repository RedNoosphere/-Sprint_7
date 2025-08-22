package ru.yandex;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

public class BaseOrderTest {

    protected OrderAPI.Order order;
    protected int trackNumber;
    protected int orderId;

    @BeforeEach
    @Step("Подготовка тестового заказа перед каждым тестом")
    public void setUp() {
        // Создаем тестовый заказ перед каждым тестом
        order = OrderAPI.createValidOrder(new String[]{"BLACK"});
        Response response = OrderAPI.createOrder(order);

        if (OrderAPI.isOrderCreatedSuccessfully(response)) {
            trackNumber = OrderAPI.getTrackNumber(response);

        }
    }

    @AfterEach
    @Step("Очистка тестовых данных заказа")
    public void tearDown() {
        // Очистка тестовых данных
        if (trackNumber != 0) {
            OrderAPI.cancelOrder(trackNumber);
        }
    }

    @Step("Создание тестового заказа с цветами: {colors}")
    protected OrderAPI.Order createOrderWithColor(String[] colors) {
        return OrderAPI.createValidOrder(colors);
    }

    @Step("Получение track number из ответа")
    protected int getTrackNumber(Response response) {
        return OrderAPI.getTrackNumber(response);
    }

    @Step("Проверка успешного создания заказа")
    protected boolean isOrderCreatedSuccessfully(Response response) {
        return OrderAPI.isOrderCreatedSuccessfully(response);
    }

    @Step("Отмена заказа с track number: {track}")
    protected Response cancelOrder(int track) {
        return OrderAPI.cancelOrder(track);
    }
}