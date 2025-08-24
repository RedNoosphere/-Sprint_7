package API;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import data.Courier;
import data.CourierCredentials;

import static io.restassured.RestAssured.given;

public class CourierAPI {

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru";
    private static final String COURIER_PATH = "/api/v1/courier";

    static {
        RestAssured.baseURI = BASE_URI;
    }

    // --- ОСНОВНЫЕ МЕТОДЫ API ---

    @Step("Создание курьера")
    public static Response createCourier(Courier courier) {
        return given()
                .contentType("application/json")
                .body(courier)
                .when()
                .post(COURIER_PATH);
    }

    @Step("Логин курьера")
    public static Response loginCourier(CourierCredentials credentials) {
        return given()
                .contentType("application/json")
                .body(credentials)
                .when()
                .post(COURIER_PATH + "/login");
    }

    // --- ПРИВАТНЫЕ ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ---

    @Step("Удаление курьера по ID")
    private static Response deleteCourierById(int id) {
        return given()
                .when()
                .delete(COURIER_PATH + "/" + id);
    }

    @Step("Проверка успешной авторизации курьера")
    private static boolean isCourierLoggedInSuccessfully(Response response) {
        return response.statusCode() == 200 && response.path("id") != null;
    }

    // --- ПУБЛИЧНЫЕ ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ---

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
            CourierCredentials credentials = new CourierCredentials(login, password);
            Response loginResponse = loginCourier(credentials);

            if (isCourierLoggedInSuccessfully(loginResponse)) {  // Теперь этот метод существует!
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

    @Step("Создание валидного курьера")
    public static Courier createValidCourier() {
        String uniqueLogin = "testCourier_" + System.currentTimeMillis();
        return new Courier(uniqueLogin, "1234", "Sasha");
    }

    @Step("Проверка успешного создания курьера")
    public static boolean isCourierCreatedSuccessfully(Response response) {
        return response.statusCode() == 201 && response.path("ok") != null && response.path("ok").equals(true);
    }

    @Step("Удаление курьера если существует")
    public static void deleteCourierIfExists(String login, String password) {
        fullDeleteCourier(login, password);
    }
}