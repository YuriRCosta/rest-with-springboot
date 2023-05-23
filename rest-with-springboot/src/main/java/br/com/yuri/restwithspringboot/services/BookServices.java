package br.com.yuri.restwithspringboot.services;

import br.com.yuri.restwithspringboot.controllers.BookController;
import br.com.yuri.restwithspringboot.data.vo.v1.BookVO;
import br.com.yuri.restwithspringboot.exceptions.RequiredObjectIsNullException;
import br.com.yuri.restwithspringboot.exceptions.ResourceNotFoundException;
import br.com.yuri.restwithspringboot.mapper.DozerMapper;
import br.com.yuri.restwithspringboot.mapper.custom.BookMapper;
import br.com.yuri.restwithspringboot.model.Book;
import br.com.yuri.restwithspringboot.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class BookServices {

    private static final String MESSAGE_NO_RECORDS = "No records found for this ID";

    @Autowired
    BookMapper mapper;

    @Autowired
    BookRepository repository;

    private final Logger logger = Logger.getLogger(BookServices.class.getName());

    public List<BookVO> findAll() {
        logger.info("Finding all books!");
        var books = DozerMapper.parseListObjects(repository.findAll(), BookVO.class);
        books.stream().forEach(p -> p.add(linkTo(methodOn(BookController.class).findById(p.getKey())).withSelfRel()));
        return books;
    }

    public BookVO create(BookVO book) {
        if (book == null) throw new RequiredObjectIsNullException("Required object is not allowed to be null");
        logger.info("Creating a new book!");
        var entity = DozerMapper.parseObject(book, Book.class);
        var vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
        vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }

    public BookVO update(BookVO book) {
        if (book == null) throw new RequiredObjectIsNullException("Required object is not allowed to be null");
        logger.info("Updating a book!");
        var entity = repository.findById(book.getKey())
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_NO_RECORDS));

        entity.setAuthor(book.getAuthor());
        entity.setPrice(book.getPrice());
        entity.setTitle(book.getTitle());
        entity.setLaunchDate(book.getLaunchDate());

        var vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
        vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }

    public BookVO findById(Long id) {
        logger.info("Finding one book!");
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_NO_RECORDS));
        var vo = DozerMapper.parseObject(entity, BookVO.class);
        vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
        return vo;
    }

    public void delete(Long id) {
        logger.info("Deleting a book!");
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_NO_RECORDS));
        repository.delete(entity);
    }
}
