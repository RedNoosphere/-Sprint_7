import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@DisplayName("Тесты на список заказов")
@Epic("API тесты для сервиса доставки")
@Feature("Получение списка заказов")
public class OrderListTest extends BaseOrderTest {

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверка, что система возвращает непустой список заказов")
    public void getOrdersList() {
        given()
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .body("orders", notNullValue());
    }

    @Test
    @DisplayName("Проверка структуры ответа")
    public void checkResponseStructure() {
        given()
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .body("orders[0].id", notNullValue())
                .body("orders[0].firstName", notNullValue())
                .body("orders[0].lastName", notNullValue())
                .body("orders[0].address", notNullValue());
    }
}