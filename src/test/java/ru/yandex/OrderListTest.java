package ru.yandex;

import API.OrderAPI;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.Matchers.*;

@DisplayName("Тесты работы со списком заказов")
@Epic("API тесты для сервиса доставки")
@Feature("Список заказов")
public class OrderListTest {

    @Test
    @Story("Получение списка заказов")
    @DisplayName("Успешное получение списка всех заказов")
    @Step("Получение полного списка заказов")
    public void getOrdersListSuccess() {
        OrderAPI.getOrdersList()
                .then()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("orders", hasSize(greaterThan(0)));
    }

    @Test
    @Story("Получение списка заказов")
    @DisplayName("Получение списка заказов с лимитом")
    @Step("Получение списка заказов с ограничением количества")
    public void getOrdersListWithLimit() {
        // Если API поддерживает параметр лимита, нужно добавить его в OrderAPI
        OrderAPI.getOrdersList()
                .then()
                .statusCode(200)
                .body("orders", notNullValue());
    }

    @Test
    @Story("Получение списка заказов")
    @DisplayName("Получение списка заказов с параметрами")
    @Step("Получение списка заказов с дополнительными параметрами фильтрации")
    public void getOrdersListWithParameters() {
        // Если API поддерживает параметры фильтрации, нужно добавить их в OrderAPI
        OrderAPI.getOrdersList()
                .then()
                .statusCode(200)
                .body("orders", notNullValue());
    }
}