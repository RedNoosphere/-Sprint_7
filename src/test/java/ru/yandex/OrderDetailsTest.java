import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@DisplayName("Тесты для деталей создания заказа")
@Epic("API тесты для сервиса доставки")
@Feature("Получение информации о заказе")
public class OrderDetailsTest extends BaseOrderTest {

    private Integer trackId;  // Изменено на Integer

    @BeforeEach
    public void setUp() {
        trackId = createTestOrder("BLACK");
    }

    @Test
    @DisplayName("Получение информации по трек-номеру")
    public void getOrderByTrack() {
        given()
                .queryParam("t", trackId)
                .get("/api/v1/orders/track")
                .then()
                .statusCode(200)
                .body("order.id", notNullValue())
                .body("order.track", equalTo(trackId));  // Сравниваем с Integer
    }

    @Test
    @DisplayName("Запрос без трек-номера")
    public void getOrderWithoutTrack() {
        given()
                .get("/api/v1/orders/track")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }
}