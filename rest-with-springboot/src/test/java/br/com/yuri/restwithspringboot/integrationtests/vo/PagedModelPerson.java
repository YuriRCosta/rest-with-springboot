package br.com.yuri.restwithspringboot.integrationtests.vo;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.List;

@XmlRootElement
public class PagedModelPerson implements Serializable {

    @XmlElement(name = "content")
    private List<PersonVO> content;

    public List<PersonVO> getContent() {
        return content;
    }

    public void setContent(List<PersonVO> content) {
        this.content = content;
    }
}
