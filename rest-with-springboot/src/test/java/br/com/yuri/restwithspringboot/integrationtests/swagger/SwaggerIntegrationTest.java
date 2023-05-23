package br.com.yuri.restwithspringboot.integrationtests.swagger;

import br.com.yuri.restwithspringboot.configs.TestConfigs;
import br.com.yuri.restwithspringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SwaggerIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void shouldDisplaySwaggerUiPage() {
        var content = given()
                .port(TestConfigs.SERVER_PORT)
                .when()
                .get("/swagger-ui.html")
                .then()
                .statusCode(200)
                .extract()
                .asString();

        Assertions.assertTrue(content.contains("Swagger UI"));
    }

}
