package tests;

import org.junit.jupiter.api.DisplayName;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import lib.ApiCoreRequests;
import lib.DataGenerator;
import lib.Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.core.IsEqual.equalTo;

public class CreateOrderTest {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    String accessToken;

    @BeforeEach
    public void createUser() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response createUser = apiCoreRequests.makePostRequest(
                Constants.REGISTER,
                userData
        );
        this.accessToken = createUser.path("accessToken");
    }

    @Test
    @DisplayName("Successful order creation scenario")
    public void testCreateOrderPositive() {
        String ingredients = Constants.VALID_INGREDIENTS;

        ValidatableResponse responseCreateOrder = apiCoreRequests.makePostRequestWithToken(
                Constants.ORDERS,
                this.accessToken,
                ingredients
        );
        responseCreateOrder.assertThat().statusCode(200)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Create order without ingredients")
    public void testCreateOrderNoIngredients() {
        ValidatableResponse responseCreateOrder = apiCoreRequests.makePostRequestWithToken(
                Constants.ORDERS,
                this.accessToken,
                ""
        );
        responseCreateOrder.assertThat().statusCode(400)
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Create order with wrong hash")
    public void testCreateOrderWrongHashReturnsError() {
        String ingredients = Constants.NOT_VALID_INGREDIENTS;

        ValidatableResponse responseCreateOrder = apiCoreRequests.makePostRequestWithToken(
                Constants.ORDERS,
                this.accessToken,
                ingredients
        );
        responseCreateOrder.assertThat().statusCode(500);
    }

    @AfterEach
    public void deleteUser() {
        apiCoreRequests.makeDeleteRequest(
                Constants.USER,
                this.accessToken
        );
    }
}
