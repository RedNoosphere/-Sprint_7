package ru.yandex;

import API.OrderAPI;
import data.Order;  // Правильный импорт!
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

public class BaseOrderTest {

    protected Order order;  // Правильный тип!
    protected int trackNumber;

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
        // Очистка тестовых данных - используем напрямую OrderAPI
        if (trackNumber != 0) {
            OrderAPI.cancelOrder(trackNumber);
        }
    }

    @Step("Создание тестового заказа с цветами: {colors}")
    protected Order createOrderWithColor(String[] colors) {
        return OrderAPI.createValidOrder(colors);
    }
}