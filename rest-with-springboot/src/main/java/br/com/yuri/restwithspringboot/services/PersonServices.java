package br.com.yuri.restwithspringboot.services;

import br.com.yuri.restwithspringboot.controllers.PersonController;
import br.com.yuri.restwithspringboot.data.vo.v1.PersonVO;
import br.com.yuri.restwithspringboot.exceptions.RequiredObjectIsNullException;
import br.com.yuri.restwithspringboot.exceptions.ResourceNotFoundException;
import br.com.yuri.restwithspringboot.mapper.DozerMapper;
import br.com.yuri.restwithspringboot.mapper.custom.PersonMapper;
import br.com.yuri.restwithspringboot.model.Person;
import br.com.yuri.restwithspringboot.repositories.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PersonServices {

    private static final String MESSAGE_NO_RECORDS = "No records found for this ID";

    @Autowired
    PersonMapper mapper;

    @Autowired
    PersonRepository repository;

    private final Logger logger = Logger.getLogger(PersonServices.class.getName());

    public List<PersonVO> findAll() {
        logger.info("Finding all persons!");
        var people = DozerMapper.parseListObjects(repository.findAll(), PersonVO.class);
        people.stream().forEach(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));
        return people;
    }

    public PersonVO create(PersonVO person) {
        if (person == null) throw new RequiredObjectIsNullException("Required object is not allowed to be null");
        logger.info("Creating a new person!");
        var entity = DozerMapper.parseObject(person, Person.class);
        var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }

    public PersonVO update(PersonVO person) {
        if (person == null) throw new RequiredObjectIsNullException("Required object is not allowed to be null");
        logger.info("Updating a person!");
        var entity = repository.findById(person.getKey())
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_NO_RECORDS));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }

    public PersonVO findById(Long id) {
        logger.info("Finding one person!");
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_NO_RECORDS));
        var vo = DozerMapper.parseObject(entity, PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return vo;
    }

    @Transactional
    public PersonVO disablePerson(Long id) {
        logger.info("Disabling one person!");
        repository.disablePerson(id);
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_NO_RECORDS));
        var vo = DozerMapper.parseObject(entity, PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return vo;
    }

    public void delete(Long id) {
        logger.info("Deleting a person!");
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_NO_RECORDS));
        repository.delete(entity);
    }
}
