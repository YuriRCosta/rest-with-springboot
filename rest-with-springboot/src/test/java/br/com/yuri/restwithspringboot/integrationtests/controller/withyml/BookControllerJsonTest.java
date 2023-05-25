package br.com.yuri.restwithspringboot.integrationtests.controller.withyml;

import br.com.yuri.restwithspringboot.configs.TestConfigs;
import br.com.yuri.restwithspringboot.integrationtests.controller.withyml.mapper.YMLMapper;
import br.com.yuri.restwithspringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.yuri.restwithspringboot.integrationtests.vo.AccountCredentialsVO;
import br.com.yuri.restwithspringboot.integrationtests.vo.BookVO;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static YMLMapper mapper;
    private static BookVO bookVO;

    @BeforeAll
    public static void setUp() {
        mapper = new YMLMapper();
        bookVO = new BookVO();
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
                .body(user, mapper)
                    .when()
                .post()
                    .then()
                        .statusCode(200)
                            .extract()
                            .body()
                                .as(TokenVO.class, mapper)
                            .getAccessToken();

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenVO)
                .setBasePath("/api/book/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(1)
    public void testCreate() throws JsonProcessingException, ParseException {
        mockBook();

        var persistedBook = given().spec(specification)
                .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
                .accept(TestConfigs.CONTENT_TYPE_YAML)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
                .body(bookVO, mapper)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(BookVO.class, mapper);

        bookVO = persistedBook;

        Assertions.assertNotNull(persistedBook);
        Assertions.assertEquals(bookVO.getTitle(), persistedBook.getTitle());
        Assertions.assertEquals(bookVO.getAuthor(), persistedBook.getAuthor());
        Assertions.assertEquals(bookVO.getPrice(), persistedBook.getPrice());
        Assertions.assertNotNull(persistedBook.getLaunchDate());

        Assertions.assertTrue(persistedBook.getId() > 0);

        Assertions.assertEquals("Michael C. Feathers", persistedBook.getAuthor());
        Assertions.assertEquals("Working effectively with legacy code", persistedBook.getTitle());
        Assertions.assertEquals(49.0, persistedBook.getPrice());
    }

    @Test
    @Order(2)
    public void testUpdate() throws JsonProcessingException {
        bookVO.setAuthor("Santos");

        var persistedBook = given().spec(specification)
                .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
                .accept(TestConfigs.CONTENT_TYPE_YAML)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
                .body(bookVO, mapper)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(BookVO.class, mapper);

        bookVO = persistedBook;

        Assertions.assertNotNull(persistedBook);
        Assertions.assertEquals(bookVO.getTitle(), persistedBook.getTitle());
        Assertions.assertEquals(bookVO.getAuthor(), persistedBook.getAuthor());
        Assertions.assertEquals(bookVO.getPrice(), persistedBook.getPrice());
        Assertions.assertNotNull(persistedBook.getLaunchDate());

        Assertions.assertTrue(persistedBook.getId() > 0);

        Assertions.assertEquals("Santos", persistedBook.getAuthor());
        Assertions.assertEquals("Working effectively with legacy code", persistedBook.getTitle());
        Assertions.assertEquals(49.0, persistedBook.getPrice());
    }

    @Test
    @Order(3)
    public void testFindById() throws JsonProcessingException, ParseException {
        mockBook();

        var persistedBook = given().spec(specification)
                .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
                .accept(TestConfigs.CONTENT_TYPE_YAML)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, "http://localhost:8888")
                .pathParam("id", bookVO.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(BookVO.class, mapper);

        bookVO = persistedBook;

        Assertions.assertNotNull(persistedBook);
        Assertions.assertNotNull(persistedBook);
        Assertions.assertEquals(bookVO.getTitle(), persistedBook.getTitle());
        Assertions.assertEquals(bookVO.getAuthor(), persistedBook.getAuthor());
        Assertions.assertEquals(bookVO.getPrice(), persistedBook.getPrice());
        Assertions.assertNotNull(persistedBook.getLaunchDate());

        Assertions.assertTrue(persistedBook.getId() > 0);

        Assertions.assertEquals("Santos", persistedBook.getAuthor());
        Assertions.assertEquals("Working effectively with legacy code", persistedBook.getTitle());
        Assertions.assertEquals(49.0, persistedBook.getPrice());
    }

    @Test
    @Order(4)
    public void testDelete() throws JsonProcessingException {
        given().spec(specification)
                .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
                .accept(TestConfigs.CONTENT_TYPE_YAML)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
                .pathParam("id", bookVO.getId())
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
                .as(BookVO[].class, mapper);
                //.as(new TypeRef<List<BookVO>>() {});

        List<BookVO> books = Arrays.asList(content);
        BookVO foundBookOne = books.get(0);

        Assertions.assertNotNull(foundBookOne.getPrice());
        Assertions.assertNotNull(foundBookOne.getTitle());
        Assertions.assertNotNull(foundBookOne.getAuthor());
        Assertions.assertNotNull(foundBookOne.getLaunchDate());

        Assertions.assertEquals(1, foundBookOne.getId());

        Assertions.assertEquals("Michael C. Feathers", foundBookOne.getAuthor());
        Assertions.assertEquals("Working effectively with legacy code", foundBookOne.getTitle());
        Assertions.assertEquals(49.0, foundBookOne.getPrice());

        BookVO foundBookTwo = books.get(1);

        Assertions.assertNotNull(foundBookTwo.getPrice());
        Assertions.assertNotNull(foundBookTwo.getTitle());
        Assertions.assertNotNull(foundBookTwo.getAuthor());
        Assertions.assertNotNull(foundBookTwo.getLaunchDate());

        Assertions.assertEquals(2, foundBookTwo.getId());

        Assertions.assertEquals("Ralph Johnson, Erich Gamma, John Vlissides e Richard Helm", foundBookTwo.getAuthor());
        Assertions.assertEquals("Design Patterns", foundBookTwo.getTitle());
        Assertions.assertEquals(45.0, foundBookTwo.getPrice());
    }

    @Test
    @Order(6)
    public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {

        RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
                .setBasePath("/api/book/v1")
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

    private void mockBook() throws ParseException {
        bookVO.setAuthor("Michael C. Feathers");
        bookVO.setTitle("Working effectively with legacy code");
        bookVO.setPrice(49.0);
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        Date data = formato.parse("23/11/2015");
        bookVO.setLaunchDate(data);
    }

}
