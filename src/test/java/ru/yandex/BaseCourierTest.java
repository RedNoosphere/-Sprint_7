import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.delete;

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
        try {
            Integer courierId = given()
                    .contentType("application/json")
                    .body(String.format("{\"login\":\"%s\",\"password\":\"%s\"}", login, password))
                    .post("/api/v1/courier/login")
                    .then()
                    .extract()
                    .path("id");

            if (courierId != null) {
                deleteCourierById(courierId);
            }
        } catch (Exception e) {
            System.out.println("Удаление курьера не потребовалось: " + e.getMessage());
        }
    }

    @Step("Удаление курьера по ID: {courierId}")
    private void deleteCourierById(Integer courierId) {
        given()
                .delete("/api/v1/courier/" + courierId);
    }

    @Step("Получение ID курьера по логину: {login}")
    protected Integer getCourierId(String login, String password) {
        try {
            return given()
                    .contentType("application/json")
                    .body(String.format("{\"login\":\"%s\",\"password\":\"%s\"}", login, password))
                    .post("/api/v1/courier/login")
                    .then()
                    .extract()
                    .path("id");
        } catch (Exception e) {
            System.out.println("Не удалось получить ID курьера: " + e.getMessage());
            return null;
        }
    }
}