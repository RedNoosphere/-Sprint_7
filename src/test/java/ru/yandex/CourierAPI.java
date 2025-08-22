package ru.yandex;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.Map;
import java.util.HashMap;

import static io.restassured.RestAssured.*;

public class CourierAPI {

    // Базовый URL и путь для всех эндпоинтов курьера
    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru";
    private static final String COURIER_PATH = "/api/v1/courier";

    static {
        baseURI = BASE_URI;
    }

    // --- ВЛОЖЕННЫЕ DTO-КЛАССЫ ---

    public static class Courier {
        private String login;
        private String password;
        private String firstName;

        public Courier() {
        }

        public Courier(String login, String password, String firstName) {
            this.login = login;
            this.password = password;
            this.firstName = firstName;
        }

        public String getLogin() { return login; }
        public String getPassword() { return password; }
        public String getFirstName() { return firstName; }

        public void setLogin(String login) { this.login = login; }
        public void setPassword(String password) { this.password = password; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
    }

    public static class CourierCredentials {
        private String login;
        private String password;

        public CourierCredentials(String login, String password) {
            this.login = login;
            this.password = password;
        }

        public String getLogin() { return login; }
        public String getPassword() { return password; }
    }

    // --- МЕТОДЫ API ---

    @Step("Создание курьера")
    public static Response createCourier(Courier courier) {
        return given()
                .contentType("application/json")
                .body(courier)
                .when()
                .post(COURIER_PATH);
    }

    @Step("Создание курьера с параметрами")
    public static Response createCourier(String login, String password, String firstName) {
        Courier courier = new Courier(login, password, firstName);
        return createCourier(courier);
    }

    @Step("Логин курьера")
    public static Response loginCourier(CourierCredentials credentials) {
        return given()
                .contentType("application/json")
                .body(credentials)
                .when()
                .post(COURIER_PATH + "/login");
    }

    @Step("Логин курьера с логином и паролем")
    public static Response loginCourier(String login, String password) {
        CourierCredentials credentials = new CourierCredentials(login, password);
        return loginCourier(credentials);
    }

    @Step("Удаление курьера по ID")
    public static Response deleteCourierById(int id) {
        return given()
                .when()
                .delete(COURIER_PATH + "/" + id);
    }

    @Step("Полное удаление курьера по логину и паролю")
    public static void fullDeleteCourier(String login, String password) {
        try {
            Integer courierId = getCourierId(login, password);
            if (courierId != null) {
                deleteCourierById(courierId);
                System.out.println("Курьер " + login + " успешно удален");
            }
        } catch (Exception e) {
            System.out.println("Удаление курьера не потребовалось: " + e.getMessage());
        }
    }

    @Step("Получение ID курьера по логину и паролю")
    public static Integer getCourierId(String login, String password) {
        try {
            Response loginResponse = loginCourier(login, password);

            if (isCourierLoggedInSuccessfully(loginResponse)) {
                return loginResponse.path("id");
            } else {
                System.out.println("Не удалось авторизоваться. Status: " + loginResponse.statusCode());
                return null;
            }
        } catch (Exception e) {
            System.out.println("Не удалось получить ID курьера: " + e.getMessage());
            return null;
        }
    }

    // --- ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ---

    @Step("Создание валидного курьера")
    public static Courier createValidCourier() {
        String uniqueLogin = "testCourier_" + System.currentTimeMillis();
        return new Courier(uniqueLogin, "1234", "Sasha");
    }

    @Step("Проверка успешного создания курьера")
    public static boolean isCourierCreatedSuccessfully(Response response) {
        return response.statusCode() == 201 && response.path("ok") != null && response.path("ok").equals(true);
    }

    @Step("Проверка успешной авторизации курьера")
    public static boolean isCourierLoggedInSuccessfully(Response response) {
        return response.statusCode() == 200 && response.path("id") != null;
    }

    @Step("Удаление курьера если существует")
    public static void deleteCourierIfExists(String login, String password) {
        fullDeleteCourier(login, password);
    }
}