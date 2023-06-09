package br.com.yuri.restwithspringboot.controllers;

import br.com.yuri.restwithspringboot.data.vo.v1.PersonVO;
import br.com.yuri.restwithspringboot.services.PersonServices;
import br.com.yuri.restwithspringboot.util.MediaType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/person/v1")
@RestController
@Tag(name = "Person Endpoint", description = "Endpoints for Managing People")
public class PersonController {

    @Autowired
    private PersonServices services;

    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Find a person recorded", description = "Find a person recorded in the database",
            tags = {"Person Endpoint"}, responses = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(schema = @Schema(implementation = PersonVO.class))),
            @ApiResponse(responseCode = "204", description = "No Content", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Error", content = @Content)})
    public PersonVO findById(@PathVariable(value = "id") Long id) {
        return services.findById(id);
    }

    @GetMapping(value = "/findPersonByName/{firstName}",
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Find a person by name recorded", description = "Find a person by name recorded in the database",
            tags = {"Person Endpoint"}, responses = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(schema = @Schema(implementation = PersonVO.class))),
            @ApiResponse(responseCode = "204", description = "No Content", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Error", content = @Content)})
    public ResponseEntity<PagedModel<EntityModel<PersonVO>>> findPersonByName(@PathVariable(value = "firstName") String firstName,
                                                                              @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                              @RequestParam(value = "size", defaultValue = "12") Integer size,
                                                                              @RequestParam(value = "direction", defaultValue = "asc") String direction) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
        return ResponseEntity.ok(services.findPersonByName(firstName, pageable));
    }


    @PatchMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Disable a person recorded", description = "Disable a person recorded in the database",
            tags = {"Person Endpoint"}, responses = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(schema = @Schema(implementation = PersonVO.class))),
            @ApiResponse(responseCode = "204", description = "No Content", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Error", content = @Content)})
    public PersonVO disablePerson(@PathVariable(value = "id") Long id) {
        return services.disablePerson(id);
    }


    @PostMapping(
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
            consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Add a new person", description = "Add a new person in the database",
            tags = {"Person Endpoint"}, responses = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(schema = @Schema(implementation = PersonVO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Error", content = @Content)})
    public PersonVO create(@RequestBody PersonVO person) {
        return services.create(person);
    }

    @GetMapping(
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Find all people recorded", description = "Find all people recorded in the database",
            tags = {"Person Endpoint"}, responses = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PersonVO.class)))}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Error", content = @Content)})
    public ResponseEntity<PagedModel<EntityModel<PersonVO>>> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                    @RequestParam(value = "size", defaultValue = "12") Integer size,
                                                                    @RequestParam(value = "direction", defaultValue = "asc") String direction) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
        return ResponseEntity.ok(services.findAll(pageable));
    }

    @PutMapping(
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
            consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Update a new person", description = "Update a new person in the database",
            tags = {"Person Endpoint"}, responses = {
            @ApiResponse(responseCode = "200", description = "Updated",
                    content = @Content(schema = @Schema(implementation = PersonVO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Error", content = @Content)})
    public PersonVO update(@RequestBody PersonVO person) {
        return services.update(person);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete a new person", description = "Delete a new person in the database",
            tags = {"Person Endpoint"}, responses = {
            @ApiResponse(responseCode = "204", description = "No Content", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Error", content = @Content)})
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
        services.delete(id);
        return ResponseEntity.noContent().build();
    }
}
