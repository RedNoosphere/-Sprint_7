package ru.yandex;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.*;

@DisplayName("Тесты создания курьера")
@Epic("API тесты для сервиса доставки")
@Feature("Создание курьера")
public class CourierCreationTest {

    private CourierAPI.Courier courier;
    private String login;

    @BeforeEach
    @Step("Подготовка тестовых данных курьера")
    public void setUp() {
        // Используем вспомогательный метод из CourierAPI
        courier = CourierAPI.createValidCourier();
        login = courier.getLogin();
    }

    @AfterEach
    @Step("Очистка тестовых данных курьера: {login}")
    public void tearDown() {
        // Очищаем через API класс
        CourierAPI.deleteCourierIfExists(login, courier.getPassword());
    }

    @Test
    @Story("Позитивные сценарии")
    @DisplayName("Успешное создание курьера")
    @Description("Проверка создания нового курьера с валидными данными")
    @Step("Создание курьера с валидными данными: логин {courier.login}")
    public void createCourierSuccess() {
        CourierAPI.createCourier(courier)
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Test
    @Story("Негативные сценарии")
    @DisplayName("Создание дубликата курьера")
    @Step("Попытка создания дубликата курьера: логин {courier.login}")
    public void createDuplicateCourierFails() {
        // Создаем первого курьера
        CourierAPI.createCourier(courier);

        // Пытаемся создать второго с теми же данными и проверяем ошибку
        CourierAPI.createCourier(courier)
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @Story("Валидация данных")
    @DisplayName("Создание без обязательного поля (password)")
    @Step("Попытка создания курьера без пароля: логин {login}")
    public void createCourierWithoutRequiredFieldFails() {
        // Создаем невалидного курьера без пароля
        CourierAPI.Courier invalidCourier = new CourierAPI.Courier(login, null, "Sasha");

        CourierAPI.createCourier(invalidCourier)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @Story("Валидация данных")
    @DisplayName("Создание без обязательного поля (login)")
    @Step("Попытка создания курьера без логина")
    public void createCourierWithoutLoginFails() {
        // Создаем невалидного курьера без логина, но с паролем
        CourierAPI.Courier invalidCourier = new CourierAPI.Courier(null, "validPassword123", "Sasha");

        CourierAPI.createCourier(invalidCourier)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @Story("Валидация данных")
    @DisplayName("Создание курьера с пустым логином")
    @Step("Попытка создания курьера с пустым логином")
    public void createCourierWithEmptyLoginFails() {
        CourierAPI.Courier invalidCourier = new CourierAPI.Courier("", "emptyPass", "Sasha");

        CourierAPI.createCourier(invalidCourier)
                .then()
                .statusCode(400);
    }

    @Test
    @Story("Валидация данных")
    @DisplayName("Создание курьера с пустым паролем")
    @Step("Попытка создания курьера с пустым паролем: логин {login}")
    public void createCourierWithEmptyPasswordFails() {
        CourierAPI.Courier invalidCourier = new CourierAPI.Courier("validLogin", "", "Sasha");

        CourierAPI.createCourier(invalidCourier)
                .then()
                .statusCode(400);
    }

    @Step("Создание курьера через API: логин {courier.login}")
    private static Response createCourier(CourierAPI.Courier courier) {
        return CourierAPI.createCourier(courier);
    }
}