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
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

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

    @Autowired
    PagedResourcesAssembler<PersonVO> assembler;

    private final Logger logger = Logger.getLogger(PersonServices.class.getName());

    public PagedModel<EntityModel<PersonVO>> findAll(Pageable pageable) {
        logger.info("Finding all persons!");

        var personPage = repository.findAll(pageable);

        var peopleVOPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
        peopleVOPage.map(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));

        Link link = linkTo(methodOn(PersonController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();
        return assembler.toModel(peopleVOPage, link);
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

    public PagedModel<EntityModel<PersonVO>> findPersonByName(String firstName, Pageable pageable) {
        logger.info("Finding people by name!");
        var personPage = repository.findPeopleByName(firstName, pageable);

        var peopleVOPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
        peopleVOPage.map(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));

        Link link = linkTo(methodOn(PersonController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();
        return assembler.toModel(peopleVOPage, link);
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
