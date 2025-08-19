import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

import static io.restassured.RestAssured.given;

public class BaseOrderTest {
    @BeforeAll
    public static void setupAll() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    protected Integer createTestOrder(String color) {  // Изменено на Integer
        return given()
                .contentType("application/json")
                .body(String.format(
                        "{ \"firstName\": \"Тест\", \"lastName\": \"Тестов\", " +
                                "\"address\": \"Москва\", \"metroStation\": 4, " +
                                "\"phone\": \"+79991234567\", \"rentTime\": 5, " +
                                "\"deliveryDate\": \"2023-12-31\", \"comment\": \"Коммент\", " +
                                "\"color\": [\"%s\"] }", color))
                .post("/api/v1/orders")
                .then()
                .extract()
                .path("track");
    }
}