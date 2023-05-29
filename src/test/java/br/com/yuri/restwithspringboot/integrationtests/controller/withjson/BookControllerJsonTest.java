package br.com.yuri.restwithspringboot.integrationtests.controller.withjson;

import br.com.yuri.restwithspringboot.configs.TestConfigs;
import br.com.yuri.restwithspringboot.data.vo.v1.security.AccountCredentialsVO;
import br.com.yuri.restwithspringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.yuri.restwithspringboot.integrationtests.vo.BookVO;
import br.com.yuri.restwithspringboot.integrationtests.vo.TokenVO;
import br.com.yuri.restwithspringboot.integrationtests.vo.WrapperBookVO;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper mapper;

    private static BookVO bookVO;

    @BeforeAll
    public static void setUp() {
        mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        bookVO = new BookVO();
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

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(bookVO)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        BookVO persistedBook = mapper.readValue(content, BookVO.class);
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

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(bookVO)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        BookVO persistedBook = mapper.readValue(content, BookVO.class);
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

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, "http://localhost:8888")
                .pathParam("id", bookVO.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        BookVO persistedBook = mapper.readValue(content, BookVO.class);
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
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
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
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .queryParams("page", 0, "size", 10, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();
                //.as(new TypeRef<List<BookVO>>() {});

        WrapperBookVO wrapper = mapper.readValue(content, WrapperBookVO.class);
        var books = wrapper.getEmbedded().getBooks();
        BookVO foundBookOne = books.get(0);

        Assertions.assertNotNull(foundBookOne.getPrice());
        Assertions.assertNotNull(foundBookOne.getTitle());
        Assertions.assertNotNull(foundBookOne.getAuthor());
        Assertions.assertNotNull(foundBookOne.getLaunchDate());

        Assertions.assertEquals(15, foundBookOne.getId());

        Assertions.assertEquals("Aguinaldo Aragon Fernandes e Vladimir Ferraz de Abreu", foundBookOne.getAuthor());
        Assertions.assertEquals("Implantando a governança de TI", foundBookOne.getTitle());
        Assertions.assertEquals(54.0, foundBookOne.getPrice());

        BookVO foundBookTwo = books.get(1);

        Assertions.assertNotNull(foundBookTwo.getPrice());
        Assertions.assertNotNull(foundBookTwo.getTitle());
        Assertions.assertNotNull(foundBookTwo.getAuthor());
        Assertions.assertNotNull(foundBookTwo.getLaunchDate());

        Assertions.assertEquals(9, foundBookTwo.getId());

        Assertions.assertEquals("Brian Goetz e Tim Peierls", foundBookTwo.getAuthor());
        Assertions.assertEquals("Java Concurrency in Practice", foundBookTwo.getTitle());
        Assertions.assertEquals(80.0, foundBookTwo.getPrice());
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
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
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
