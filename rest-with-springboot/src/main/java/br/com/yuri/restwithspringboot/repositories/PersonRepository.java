package br.com.yuri.restwithspringboot.repositories;

import br.com.yuri.restwithspringboot.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
