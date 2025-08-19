import io.qameta.allure.*;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@DisplayName("Тесты для авторизации курьера")
@Epic("API тесты для сервиса доставки")
@Feature("Авторизация курьера")
public class CourierLoginTest extends BaseCourierTest {

    @BeforeEach
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
    public void tearDown() {
        deleteCourier(login, password);
    }

    @Test
    @Story("Позитивные сценарии")
    @DisplayName("Успешная авторизация")
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
    public void loginWithInvalidCredentialsFails() {
        given()
                .contentType("application/json")
                .body(String.format("{\"login\":\"%s\",\"password\":\"wrong_pass\"}", login))
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}