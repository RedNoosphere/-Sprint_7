import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

import static io.restassured.RestAssured.given; // Добавляем статический импорт
import static io.restassured.RestAssured.delete; // Импорт для delete()

public class BaseCourierTest {
    protected String login;
    protected final String password = "password123";
    protected final String firstName = "Тестовый";

    @BeforeAll
    public static void setupAll() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    protected void deleteCourier(String login, String password) {
        try {
            Integer courierId = given() // Теперь метод будет доступен
                    .contentType("application/json")
                    .body(String.format("{\"login\":\"%s\",\"password\":\"%s\"}", login, password))
                    .post("/api/v1/courier/login")
                    .then()
                    .extract()
                    .path("id");

            if (courierId != null) {
                given()
                        .delete("/api/v1/courier/" + courierId);
            }
        } catch (Exception e) {
            System.out.println("Удаление курьера не потребовалось: " + e.getMessage());
        }
    }
}