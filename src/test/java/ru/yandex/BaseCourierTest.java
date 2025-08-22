package ru.yandex;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class BaseCourierTest {
    protected String login;
    protected final String password = "password123";
    protected final String firstName = "Тестовый";

    @BeforeAll
    @Step("Базовая настройка тестов: установка базового URI")
    public static void setupAll() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Step("Удаление курьера с логином: {login}")
    protected void deleteCourier(String login, String password) {
        CourierAPI.fullDeleteCourier(login, password);
    }

    @Step("Получение ID курьера по логину: {login}")
    protected Integer getCourierId(String login, String password) {
        return CourierAPI.getCourierId(login, password);
    }
}