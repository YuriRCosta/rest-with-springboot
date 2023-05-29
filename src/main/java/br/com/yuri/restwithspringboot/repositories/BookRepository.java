package br.com.yuri.restwithspringboot.repositories;

import br.com.yuri.restwithspringboot.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
