package br.com.yuri.restwithspringboot.integrationtests.controller.withyml;

import br.com.yuri.restwithspringboot.configs.TestConfigs;
import br.com.yuri.restwithspringboot.integrationtests.controller.withyml.mapper.YMLMapper;
import br.com.yuri.restwithspringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.yuri.restwithspringboot.integrationtests.vo.AccountCredentialsVO;
import br.com.yuri.restwithspringboot.integrationtests.vo.PersonVO;
import br.com.yuri.restwithspringboot.integrationtests.vo.TokenVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static YMLMapper YMLMapper;
    private static PersonVO personVO;

    @BeforeAll
    public static void setUp() {
        YMLMapper = new YMLMapper();
        personVO = new PersonVO();
    }

    @Test
    @Order(0)
    public void authorization() throws JsonProcessingException {
        AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

        var tokenVO = given()
                .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
                .accept(TestConfigs.CONTENT_TYPE_YAML)
                .basePath("/auth/signin")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(TestConfigs.CONTENT_TYPE_YAML)
                .body(user, YMLMapper)
                    .when()
                .post()
                    .then()
                        .statusCode(200)
                            .extract()
                            .body()
                                .as(TokenVO.class, YMLMapper)
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

        var persistedPerson = given().spec(specification)
                .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
                .accept(TestConfigs.CONTENT_TYPE_YAML)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
                .body(personVO, YMLMapper)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(PersonVO.class, YMLMapper);

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
    public void testUpdate() throws JsonProcessingException {
        personVO.setLastName("Santos 2");

        var persistedPerson = given().spec(specification)
                .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
                .accept(TestConfigs.CONTENT_TYPE_YAML)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
                .body(personVO, YMLMapper)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(PersonVO.class, YMLMapper);

        personVO = persistedPerson;

        Assertions.assertNotNull(persistedPerson);
        Assertions.assertEquals(personVO.getFirstName(), persistedPerson.getFirstName());
        Assertions.assertEquals(personVO.getLastName(), persistedPerson.getLastName());
        Assertions.assertEquals(personVO.getAddress(), persistedPerson.getAddress());
        Assertions.assertEquals(personVO.getGender(), persistedPerson.getGender());

        Assertions.assertEquals(personVO.getId() ,persistedPerson.getId());

        Assertions.assertEquals("Yuri", persistedPerson.getFirstName());
        Assertions.assertEquals("Santos 2", persistedPerson.getLastName());
        Assertions.assertEquals("Rua dos Bobos, 0", persistedPerson.getAddress());
        Assertions.assertEquals("Male", persistedPerson.getGender());
    }

    @Test
    @Order(3)
    public void testFindById() throws JsonProcessingException {
        mockPerson();

        var persistedPerson = given().spec(specification)
                .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
                .accept(TestConfigs.CONTENT_TYPE_YAML)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, "http://localhost:8888")
                .pathParam("id", personVO.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(PersonVO.class, YMLMapper);

        personVO = persistedPerson;

        Assertions.assertNotNull(persistedPerson);
        Assertions.assertEquals(personVO.getFirstName(), persistedPerson.getFirstName());
        Assertions.assertEquals(personVO.getLastName(), persistedPerson.getLastName());
        Assertions.assertEquals(personVO.getAddress(), persistedPerson.getAddress());
        Assertions.assertEquals(personVO.getGender(), persistedPerson.getGender());

        Assertions.assertEquals(personVO.getId() ,persistedPerson.getId());

        Assertions.assertEquals("Yuri", persistedPerson.getFirstName());
        Assertions.assertEquals("Santos 2", persistedPerson.getLastName());
        Assertions.assertEquals("Rua dos Bobos, 0", persistedPerson.getAddress());
        Assertions.assertEquals("Male", persistedPerson.getGender());
    }

    @Test
    @Order(4)
    public void testDelete() throws JsonProcessingException {
        given().spec(specification)
                .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
                .accept(TestConfigs.CONTENT_TYPE_YAML)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
                .pathParam("id", personVO.getId())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(5)
    public void testFindAll() throws JsonProcessingException {
        var content = given().spec(specification)
                .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
                .accept(TestConfigs.CONTENT_TYPE_YAML)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(PersonVO[].class, YMLMapper);
                //.as(new TypeRef<List<PersonVO>>() {});

        List<PersonVO> people = Arrays.asList(content);
        PersonVO foundPersonOne = people.get(0);

        Assertions.assertNotNull(foundPersonOne.getFirstName());
        Assertions.assertNotNull(foundPersonOne.getLastName());
        Assertions.assertNotNull(foundPersonOne.getAddress());
        Assertions.assertNotNull(foundPersonOne.getGender());

        Assertions.assertEquals(1, foundPersonOne.getId());

        Assertions.assertEquals("Yuri", foundPersonOne.getFirstName());
        Assertions.assertEquals("Costa", foundPersonOne.getLastName());
        Assertions.assertEquals("Sao Paulo", foundPersonOne.getAddress());
        Assertions.assertEquals("Male", foundPersonOne.getGender());

        PersonVO foundPersonTwo = people.get(1);

        Assertions.assertNotNull(foundPersonTwo.getFirstName());
        Assertions.assertNotNull(foundPersonTwo.getLastName());
        Assertions.assertNotNull(foundPersonTwo.getAddress());
        Assertions.assertNotNull(foundPersonTwo.getGender());

        Assertions.assertEquals(3, foundPersonTwo.getId());

        Assertions.assertEquals("Yasmin", foundPersonTwo.getFirstName());
        Assertions.assertEquals("Ferreira Dadda", foundPersonTwo.getLastName());
        Assertions.assertEquals("Capao da Canoa", foundPersonTwo.getAddress());
        Assertions.assertEquals("Female", foundPersonTwo.getGender());
    }

    @Test
    @Order(6)
    public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {

        RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        given().spec(specificationWithoutToken)
                .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
                .accept(TestConfigs.CONTENT_TYPE_YAML)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
                .when()
                .get()
                .then()
                .statusCode(403);
    }

    private void mockPerson() {
        personVO.setFirstName("Yuri");
        personVO.setLastName("Santos");
        personVO.setAddress("Rua dos Bobos, 0");
        personVO.setGender("Male");
    }

}
