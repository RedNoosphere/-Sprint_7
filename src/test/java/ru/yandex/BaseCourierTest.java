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
}