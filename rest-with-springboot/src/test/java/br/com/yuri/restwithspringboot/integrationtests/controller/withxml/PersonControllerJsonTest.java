package br.com.yuri.restwithspringboot.integrationtests.controller.withxml;

import br.com.yuri.restwithspringboot.configs.TestConfigs;
import br.com.yuri.restwithspringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.yuri.restwithspringboot.integrationtests.vo.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
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
public class PersonControllerJsonTest extends AbstractIntegrationTest {

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
                    .contentType(TestConfigs.CONTENT_TYPE_XML)
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
                .contentType(TestConfigs.CONTENT_TYPE_XML)
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
        Assertions.assertTrue(persistedPerson.getEnabled());

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

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
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
        Assertions.assertTrue(persistedPerson.getEnabled());

        Assertions.assertEquals(personVO.getId() ,persistedPerson.getId());

        Assertions.assertEquals("Yuri", persistedPerson.getFirstName());
        Assertions.assertEquals("Santos 2", persistedPerson.getLastName());
        Assertions.assertEquals("Rua dos Bobos, 0", persistedPerson.getAddress());
        Assertions.assertEquals("Male", persistedPerson.getGender());
    }

    @Test
    @Order(3)
    public void testDisablePersonById() throws JsonProcessingException {
        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, "http://localhost:8888")
                .pathParam("id", personVO.getId())
                .when()
                .patch("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        PersonVO persistedPerson = mapper.readValue(content, PersonVO.class);
        personVO = persistedPerson;

        Assertions.assertNotNull(persistedPerson);
        Assertions.assertNotNull(persistedPerson.getFirstName());
        Assertions.assertNotNull(persistedPerson.getLastName());
        Assertions.assertNotNull(persistedPerson.getAddress());
        Assertions.assertNotNull(persistedPerson.getGender());
        Assertions.assertFalse(persistedPerson.getEnabled());

        Assertions.assertEquals(personVO.getId() ,persistedPerson.getId());

        Assertions.assertEquals("Yuri", persistedPerson.getFirstName());
        Assertions.assertEquals("Santos 2", persistedPerson.getLastName());
        Assertions.assertEquals("Rua dos Bobos, 0", persistedPerson.getAddress());
        Assertions.assertEquals("Male", persistedPerson.getGender());
    }

    @Test
    @Order(4)
    public void testFindById() throws JsonProcessingException {
        mockPerson();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
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
        Assertions.assertFalse(persistedPerson.getEnabled());

        Assertions.assertEquals(personVO.getId() ,persistedPerson.getId());

        Assertions.assertEquals("Yuri", persistedPerson.getFirstName());
        Assertions.assertEquals("Santos 2", persistedPerson.getLastName());
        Assertions.assertEquals("Rua dos Bobos, 0", persistedPerson.getAddress());
        Assertions.assertEquals("Male", persistedPerson.getGender());
    }

    @Test
    @Order(5)
    public void testDelete() throws JsonProcessingException {
        given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .pathParam("id", personVO.getId())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(6)
    public void testFindAll() throws JsonProcessingException {
        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .queryParams("page", 0, "size", 10, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();
                //.as(new TypeRef<List<PersonVO>>() {});

        PagedModelPerson wrapper = mapper.readValue(content, PagedModelPerson.class);
        var people = wrapper.getContent();
        PersonVO foundPersonOne = people.get(0);

        Assertions.assertNotNull(foundPersonOne.getFirstName());
        Assertions.assertNotNull(foundPersonOne.getLastName());
        Assertions.assertNotNull(foundPersonOne.getAddress());
        Assertions.assertNotNull(foundPersonOne.getGender());

        Assertions.assertEquals(586, foundPersonOne.getId());

        Assertions.assertEquals("Abby", foundPersonOne.getFirstName());
        Assertions.assertEquals("Martinovic", foundPersonOne.getLastName());
        Assertions.assertEquals("61 Southridge Drive", foundPersonOne.getAddress());
        Assertions.assertEquals("Female", foundPersonOne.getGender());

        PersonVO foundPersonTwo = people.get(1);

        Assertions.assertNotNull(foundPersonTwo.getFirstName());
        Assertions.assertNotNull(foundPersonTwo.getLastName());
        Assertions.assertNotNull(foundPersonTwo.getAddress());
        Assertions.assertNotNull(foundPersonTwo.getGender());

        Assertions.assertEquals(359, foundPersonTwo.getId());

        Assertions.assertEquals("Abel", foundPersonTwo.getFirstName());
        Assertions.assertEquals("Tukely", foundPersonTwo.getLastName());
        Assertions.assertEquals("90635 Ridgeway Terrace", foundPersonTwo.getAddress());
        Assertions.assertEquals("Male", foundPersonTwo.getGender());
    }

    @Test
    @Order(7)
    public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {

        RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        given().spec(specificationWithoutToken)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
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
        personVO.setEnabled(true);
    }

}
