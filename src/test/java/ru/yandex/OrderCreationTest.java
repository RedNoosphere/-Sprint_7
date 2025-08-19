import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;  // Добавлен этот импорт
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@DisplayName("Тесты для создания заказа")
@Epic("API тесты для сервиса доставки")
@Feature("Создание заказа")
public class OrderCreationTest extends BaseOrderTest {

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
    @Story("Варианты цветов")
    @DisplayName("Создание заказа с разными цветами")
    @Description("Проверка создания заказа с различными вариантами цветов: BLACK, GREY, оба цвета, без цвета")
    public void createOrderWithColors(String color) {
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
    }

    @Test
    @Story("Обязательные поля")
    @DisplayName("Создание заказа с минимальными данными")
    public void createOrderWithMinimumData() {
        given()
                .contentType("application/json")
                .body("{ \"firstName\": \"Тест\", \"lastName\": \"Тестов\", " +
                        "\"address\": \"Москва\", \"metroStation\": 4, " +
                        "\"phone\": \"+79991234567\", \"rentTime\": 1 }")
                .post("/api/v1/orders")
                .then()
                .statusCode(201)
                .body("track", notNullValue());
    }
}