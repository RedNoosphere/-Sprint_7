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
        // Создаем курьера для тестов авторизации
        given()
                .contentType("application/json")
                .body(String.format(
                        "{\"login\":\"%s\",\"password\":\"%s\",\"firstName\":\"%s\"}",
                        login, password, firstName))
                .post("/api/v1/courier");
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
        given()
                .contentType("application/json")
                .body(String.format("{\"login\":\"%s\",\"password\":\"%s\"}", login, password))
                .post("/api/v1/courier/login")
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
        given()
                .contentType("application/json")
                .body(String.format("{\"login\":\"%s\",\"password\":\"wrong_pass\"}", login))
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Step("Создание тестового курьера: логин {login}, имя {firstName}")
    private void createTestCourier(String login, String password, String firstName) {
        given()
                .contentType("application/json")
                .body(String.format(
                        "{\"login\":\"%s\",\"password\":\"%s\",\"firstName\":\"%s\"}",
                        login, password, firstName))
                .post("/api/v1/courier");
    }

    @Step("Авторизация курьера: логин {login}")
    private Response loginCourier(String login, String password) {
        return given()
                .contentType("application/json")
                .body(String.format("{\"login\":\"%s\",\"password\":\"%s\"}", login, password))
                .post("/api/v1/courier/login");
    }
}