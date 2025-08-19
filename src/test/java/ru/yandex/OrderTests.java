import io.qameta.allure.*;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("API тесты для сервиса доставки")
@Feature("Работа с заказами")
@DisplayName("Тесты API для работы с заказами")
public class OrderTests {

    @BeforeAll
    public static void setupAll() {
        RestAssured.filters(new AllureRestAssured());
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    static Stream<Arguments> colorDataProvider() {
        return Stream.of(
                Arguments.of("BLACK"),
                Arguments.of("GREY"),
                Arguments.of("BLACK, GREY"),
                Arguments.of("")
        );
    }

    @ParameterizedTest(name = "Цвет: {0}")
    @MethodSource("colorDataProvider")
    @Story("Создание заказа")
    @DisplayName("Создание заказа с разными цветами")
    @Description("Проверка создания заказа с различными вариантами цветов: BLACK, GREY, оба цвета, без цвета")
        @Link(name = "Документация API", url = "https://docs.qa-scooter.praktikum-services.ru")
    public void createOrderWithColors(String color) {
        step("Подготовка тестовых данных", () -> {
            step("Цвет самоката: " + color);
        });

        step("Отправка запроса на создание заказа", () -> {
            given()
                    .contentType("application/json")
                    .body(String.format(
                            "{ \"firstName\": \"Тест\", \"lastName\": \"Тестов\", " +
                                    "\"address\": \"Москва\", \"metroStation\": 4, " +
                                    "\"phone\": \"+79991234567\", \"rentTime\": 5, " +
                                    "\"deliveryDate\": \"2023-12-31\", \"comment\": \"Коммент\", " +
                                    "\"color\": [\"%s\"] }", color))
                    .post("/api/v1/orders")
                    .then()
                    .statusCode(201)
                    .body("track", notNullValue());
        });

        step("Проверка ответа", () -> {
            // Дополнительные проверки при необходимости
        });
    }

    @Test
    @Story("Получение заказов")
    @DisplayName("Получение списка заказов")
    @Description("Проверка, что система возвращает непустой список заказов")
        public void getOrdersList() {
        step("Отправка GET запроса для получения списка заказов", () -> {
            given()
                    .get("/api/v1/orders")
                    .then()
                    .statusCode(200)
                    .body("orders", notNullValue());
        });
    }
}