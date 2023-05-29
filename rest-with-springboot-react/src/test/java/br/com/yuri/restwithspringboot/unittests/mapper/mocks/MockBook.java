package br.com.yuri.restwithspringboot.unittests.mapper.mocks;

import br.com.yuri.restwithspringboot.data.vo.v1.BookVO;
import br.com.yuri.restwithspringboot.model.Book;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MockBook {

    public Book mockEntity() throws ParseException {
        return mockEntity(0);
    }

    public BookVO mockVO() throws ParseException {
        return mockVO(0);
    }

    public List<Book> mockEntityList() throws ParseException {
        List<Book> books = new ArrayList<Book>();
        for (int i = 0; i < 14; i++) {
            books.add(mockEntity(i));
        }
        return books;
    }

    public List<BookVO> mockVOList() throws ParseException {
        List<BookVO> books = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            books.add(mockVO(i));
        }
        return books;
    }

    public Book mockEntity(Integer number) throws ParseException {
        Book book = new Book();
        book.setTitle("Title Test" + number);
        book.setAuthor("Author Test" + number);
        book.setPrice(25D);
        book.setId(number.longValue());
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        Date data = formato.parse("23/11/2015");
        book.setLaunchDate(data);
        return book;
    }

    public BookVO mockVO(Integer number) throws ParseException {
        BookVO book = new BookVO();
        book.setTitle("Title Test" + number);
        book.setAuthor("Author Test" + number);
        book.setPrice(25D);
        book.setKey(number.longValue());
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        Date data = formato.parse("23/11/2015");
        book.setLaunchDate(data);
        return book;
    }

}
