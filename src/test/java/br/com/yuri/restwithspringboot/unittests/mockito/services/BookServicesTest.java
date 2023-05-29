package br.com.yuri.restwithspringboot.unittests.mockito.services;

import br.com.yuri.restwithspringboot.data.vo.v1.BookVO;
import br.com.yuri.restwithspringboot.exceptions.RequiredObjectIsNullException;
import br.com.yuri.restwithspringboot.model.Book;
import br.com.yuri.restwithspringboot.repositories.BookRepository;
import br.com.yuri.restwithspringboot.services.BookServices;
import br.com.yuri.restwithspringboot.unittests.mapper.mocks.MockBook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.text.ParseException;
import java.util.Optional;

import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookServicesTest {

    MockBook input;

    @InjectMocks
    BookServices service;

    @Mock
    BookRepository repository;

    @BeforeEach
    void setUpMocks() {
        input = new MockBook();
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void create() throws ParseException {
        Book entity = input.mockEntity(1);
        entity.setId(1L);

        Book persisted = entity;
        persisted.setId(1L);

        BookVO vo = input.mockVO(1);
        vo.setKey(1L);

        when(repository.save(entity)).thenReturn(persisted);

        var result = service.create(vo);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getKey());
        Assertions.assertNotNull(result.getLinks());
        Assertions.assertTrue(result.toString().contains("</api/book/v1/1>;rel=\"self\""));
        Assertions.assertEquals("Author Test1", result.getAuthor());
        Assertions.assertEquals("Title Test1", result.getTitle());
        Assertions.assertEquals(25D, result.getPrice());
        Assertions.assertNotNull(result.getLaunchDate());
    }

    @Test
    void createWithNullBook() {
        Exception exception = Assertions.assertThrows(RequiredObjectIsNullException.class, () -> {
            service.create(null);
        });

        String expectedMessage = "Required object is not allowed to be null";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void update() throws ParseException {
        Book entity = input.mockEntity(1);
        entity.setId(1L);

        Book persisted = entity;
        persisted.setId(1L);

        BookVO vo = input.mockVO(1);
        vo.setKey(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(persisted);

        var result = service.update(vo);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getKey());
        Assertions.assertNotNull(result.getLinks());
        Assertions.assertTrue(result.toString().contains("</api/book/v1/1>;rel=\"self\""));
        Assertions.assertEquals("Author Test1", result.getAuthor());
        Assertions.assertEquals("Title Test1", result.getTitle());
        Assertions.assertEquals(25D, result.getPrice());
        Assertions.assertNotNull(result.getLaunchDate());
    }

    @Test
    void updateWithNullBook() {
        Exception exception = Assertions.assertThrows(RequiredObjectIsNullException.class, () -> {
            service.update(null);
        });

        String expectedMessage = "Required object is not allowed to be null";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void findById() throws ParseException {
        Book entity = input.mockEntity(1);
        entity.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        var result = service.findById(1L);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getKey());
        Assertions.assertNotNull(result.getLinks());
        Assertions.assertTrue(result.toString().contains("</api/book/v1/1>;rel=\"self\""));
        Assertions.assertEquals("Author Test1", result.getAuthor());
        Assertions.assertEquals("Title Test1", result.getTitle());
        Assertions.assertEquals(25D, result.getPrice());
        Assertions.assertNotNull(result.getLaunchDate());
    }

    @Test
    void delete() throws ParseException {
        Book entity = input.mockEntity(1);
        entity.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);
    }
}