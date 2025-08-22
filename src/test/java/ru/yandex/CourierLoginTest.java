package ru.yandex;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@DisplayName("Тесты для авторизации курьера")
@Epic("API тесты для сервиса доставки")
@Feature("Авторизация курьера")
public class CourierLoginTest extends BaseCourierTest {

    @BeforeEach
    @Step("Подготовка тестового курьера для авторизации")
    public void setUp() {
        login = "testCourier_" + System.currentTimeMillis();
        // ✅ Использование API класса вместо прямого REST вызова
        CourierAPI.createCourier(login, password, firstName);
    }

    @AfterEach
    @Step("Очистка тестовых данных курьера: {login}")
    public void tearDown() {
        deleteCourier(login, password);
    }

    @Test
    @Story("Позитивные сценарии")
    @DisplayName("Успешная авторизация")
    @Step("Успешная авторизация курьера: логин {login}")
    public void loginCourierSuccess() {
        CourierAPI.loginCourier(login, password)
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
        CourierAPI.loginCourier(login, "wrong_pass")
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}