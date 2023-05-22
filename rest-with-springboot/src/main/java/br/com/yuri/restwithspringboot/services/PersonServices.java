package br.com.yuri.restwithspringboot.services;

import br.com.yuri.restwithspringboot.data.vo.v1.PersonVO;
import br.com.yuri.restwithspringboot.exceptions.ResourceNotFoundException;
import br.com.yuri.restwithspringboot.mapper.DozerMapper;
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

    public List<PersonVO> findAll() {
        logger.info("Finding all persons!");
        return DozerMapper.parseListObjects(repository.findAll(), PersonVO.class);
    }

    public PersonVO create(PersonVO person) {
        logger.info("Creating a new person!");
        var entity = DozerMapper.parseObject(person, Person.class);
        return DozerMapper.parseObject(repository.save(entity), PersonVO.class);
    }

    public PersonVO update(PersonVO person) {
        logger.info("Updating a person!");
        var entity = repository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_NO_RECORDS));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        return DozerMapper.parseObject(repository.save(entity), PersonVO.class);
    }

    public PersonVO findById(Long id) {
        logger.info("Finding one person!");
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_NO_RECORDS));
        return DozerMapper.parseObject(entity, PersonVO.class);
    }

    public void delete(Long id) {
        logger.info("Deleting a person!");
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_NO_RECORDS));
        repository.delete(entity);
    }
}
