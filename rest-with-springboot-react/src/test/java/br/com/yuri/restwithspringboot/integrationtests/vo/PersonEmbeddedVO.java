package br.com.yuri.restwithspringboot.integrationtests.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@XmlRootElement
public class PersonEmbeddedVO implements Serializable {

    @JsonProperty("personVOList")
    private List<PersonVO> people;

    public PersonEmbeddedVO() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonEmbeddedVO that = (PersonEmbeddedVO) o;
        return Objects.equals(people, that.people);
    }

    @Override
    public int hashCode() {
        return Objects.hash(people);
    }

    public List<PersonVO> getPeople() {
        return people;
    }

    public void setPeople(List<PersonVO> people) {
        this.people = people;
    }
}
