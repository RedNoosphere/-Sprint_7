package ru.yandex;

import API.CourierAPI;
import data.Courier;  // Добавлен импорт!
import data.CourierCredentials;  // Добавлен импорт!
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static org.hamcrest.Matchers.*;

@DisplayName("Тесты для авторизации курьера")
@Epic("API тесты для сервиса доставки")
@Feature("Авторизация курьера")
public class CourierLoginTest extends BaseCourierTest {

    private String login;
    private String password = "1234";  // Добавлено поле password
    private String firstName = "Sasha";  // Добавлено поле firstName

    @BeforeEach
    @Step("Подготовка тестового курьера для авторизации")
    public void setUp() {
        login = "testCourier_" + System.currentTimeMillis();
        // использование API класса через объект Courier
        Courier courier = new Courier(login, password, firstName);
        CourierAPI.createCourier(courier);
    }

    @AfterEach
    @Step("Очистка тестовых данных курьера: {login}")
    public void tearDown() {
        // Предполагается, что метод deleteCourierIfExists есть в BaseCourierTest или CourierAPI
        CourierAPI.deleteCourierIfExists(login, password);
    }

    @Test
    @Story("Позитивные сценарии")
    @DisplayName("Успешная авторизация")
    @Step("Успешная авторизация курьера: логин {login}")
    public void loginCourierSuccess() {
        // использование API класса через объект CourierCredentials
        CourierCredentials credentials = new CourierCredentials(login, password);
        CourierAPI.loginCourier(credentials)
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @Story("Негативные сценарии")
    @DisplayName("Авторизация с неверным паролем")
    @Severity(SeverityLevel.CRITICAL)
    @Step("Попытка авторизации с неверным паролем: логин {login}")
    public void loginWithInvalidCredentialsFails() {
        // использование API класса через объект CourierCredentials
        CourierCredentials credentials = new CourierCredentials(login, "wrong_pass");
        CourierAPI.loginCourier(credentials)
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @Story("Негативные сценарии")
    @DisplayName("Авторизация несуществующего курьера")
    @Step("Попытка авторизации несуществующего курьера")
    public void loginNonExistentCourierFails() {
        CourierCredentials credentials = new CourierCredentials("nonexistent", "password");
        CourierAPI.loginCourier(credentials)
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}