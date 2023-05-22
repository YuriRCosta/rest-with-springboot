package br.com.yuri.restwithspringboot.services;

import br.com.yuri.restwithspringboot.exceptions.ResourceNotFoundException;
import br.com.yuri.restwithspringboot.model.Person;
import br.com.yuri.restwithspringboot.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class PersonServices {

    private static final String MESSAGE_NO_RECORDS = "No records found for this ID";

    @Autowired
    PersonRepository repository;

    private final Logger logger = Logger.getLogger(PersonServices.class.getName());

    public List<Person> findAll() {
        logger.info("Finding all persons!");
        return repository.findAll();
    }

    public Person create(Person person) {
        logger.info("Creating a new person!");
        return repository.save(person);
    }

    public Person update(Person person) {
        logger.info("Updating a person!");
        Person entity = repository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_NO_RECORDS));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        return repository.save(entity);
    }

    public Person findById(Long id) {
        logger.info("Finding one person!");
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_NO_RECORDS));
    }

    public void delete(Long id) {
        logger.info("Deleting a person!");
        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_NO_RECORDS));
        repository.delete(entity);
    }
}
