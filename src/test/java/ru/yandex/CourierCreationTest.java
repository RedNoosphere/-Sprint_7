import io.qameta.allure.*;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@DisplayName("Тесты создания курьера")
@Epic("API тесты для сервиса доставки")
@Feature("Создание курьера")
public class CourierCreationTest extends BaseCourierTest {

    @BeforeEach
    public void setUp() {
        login = "testCourier_" + System.currentTimeMillis();
    }

    @AfterEach
    public void tearDown() {
        deleteCourier(login, password);
    }

    @Test
    @Story("Позитивные сценарии")
    @DisplayName("Успешное создание курьера")
    @Description("Проверка создания нового курьера с валидными данными")
    public void createCourierSuccess() {
        given()
                .contentType("application/json")
                .body(String.format(
                        "{\"login\":\"%s\",\"password\":\"%s\",\"firstName\":\"%s\"}",
                        login, password, firstName))
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Test
    @Story("Негативные сценарии")
    @DisplayName("Создание дубликата курьера")
    public void createDuplicateCourierFails() {
        createCourierSuccess();

        given()
                .contentType("application/json")
                .body(String.format(
                        "{\"login\":\"%s\",\"password\":\"%s\",\"firstName\":\"%s\"}",
                        login, password, firstName))
                .post("/api/v1/courier")
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @Story("Валидация данных")
    @DisplayName("Создание без обязательного поля")
    public void createCourierWithoutRequiredFieldFails() {
        given()
                .contentType("application/json")
                .body(String.format("{\"login\":\"%s\",\"firstName\":\"%s\"}", login, firstName))
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}