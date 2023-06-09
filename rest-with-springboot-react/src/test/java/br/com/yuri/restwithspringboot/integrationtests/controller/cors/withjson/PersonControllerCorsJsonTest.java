package br.com.yuri.restwithspringboot.integrationtests.controller.cors.withjson;

import br.com.yuri.restwithspringboot.configs.TestConfigs;
import br.com.yuri.restwithspringboot.data.vo.v1.security.AccountCredentialsVO;
import br.com.yuri.restwithspringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.yuri.restwithspringboot.integrationtests.vo.PersonVO;
import br.com.yuri.restwithspringboot.integrationtests.vo.TokenVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerCorsJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper mapper;

    private static PersonVO personVO;

    @BeforeAll
    public static void setUp() {
        mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        personVO = new PersonVO();
    }

    @Test
    @Order(0)
    public void authorization() throws JsonProcessingException {
        AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

        var tokenVO = given()
                .basePath("/auth/signin")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(user)
                    .when()
                .post()
                    .then()
                        .statusCode(200)
                            .extract()
                            .body()
                                .as(TokenVO.class)
                            .getAccessToken();

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenVO)
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

        @Test
    @Order(1)
    public void testCreate() throws JsonProcessingException {
        mockPerson();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, "http://localhost:8888")
                .body(personVO)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        PersonVO persistedPerson = mapper.readValue(content, PersonVO.class);
        personVO = persistedPerson;

        Assertions.assertNotNull(persistedPerson);
        Assertions.assertEquals(personVO.getFirstName(), persistedPerson.getFirstName());
        Assertions.assertEquals(personVO.getLastName(), persistedPerson.getLastName());
        Assertions.assertEquals(personVO.getAddress(), persistedPerson.getAddress());
        Assertions.assertEquals(personVO.getGender(), persistedPerson.getGender());

        Assertions.assertTrue(persistedPerson.getId() > 0);

        Assertions.assertEquals("Yuri", persistedPerson.getFirstName());
        Assertions.assertEquals("Santos", persistedPerson.getLastName());
        Assertions.assertEquals("Rua dos Bobos, 0", persistedPerson.getAddress());
        Assertions.assertEquals("Male", persistedPerson.getGender());
    }

    @Test
    @Order(2)
    public void testCreateWithWrongOrigin() throws JsonProcessingException {
        mockPerson();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_TESTE_ERRO)
                .body(personVO)
                .when()
                .post()
                .then()
                .statusCode(403)
                .extract()
                .body()
                .asString();

        Assertions.assertNotNull(content);
        Assertions.assertEquals("Invalid CORS request", content);
    }

    @Test
    @Order(3)
    public void testFindById() throws JsonProcessingException {
        mockPerson();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, "http://localhost:8888")
                .pathParam("id", personVO.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        PersonVO persistedPerson = mapper.readValue(content, PersonVO.class);
        personVO = persistedPerson;

        Assertions.assertNotNull(persistedPerson);
        Assertions.assertEquals(personVO.getFirstName(), persistedPerson.getFirstName());
        Assertions.assertEquals(personVO.getLastName(), persistedPerson.getLastName());
        Assertions.assertEquals(personVO.getAddress(), persistedPerson.getAddress());
        Assertions.assertEquals(personVO.getGender(), persistedPerson.getGender());

        Assertions.assertTrue(persistedPerson.getId() > 0);

        Assertions.assertEquals("Yuri", persistedPerson.getFirstName());
        Assertions.assertEquals("Santos", persistedPerson.getLastName());
        Assertions.assertEquals("Rua dos Bobos, 0", persistedPerson.getAddress());
        Assertions.assertEquals("Male", persistedPerson.getGender());
    }

    @Test
    @Order(4)
    public void testFindByIdWithWrongOrigin() throws JsonProcessingException {
        mockPerson();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_TESTE_ERRO)
                .pathParam("id", personVO.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(403)
                .extract()
                .body()
                .asString();

        Assertions.assertNotNull(content);
        Assertions.assertEquals("Invalid CORS request", content);
    }

    private void mockPerson() {
        personVO.setFirstName("Yuri");
        personVO.setLastName("Santos");
        personVO.setAddress("Rua dos Bobos, 0");
        personVO.setGender("Male");
        personVO.setEnabled(true);
    }

}
