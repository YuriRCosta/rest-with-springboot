package br.com.yuri.restwithspringboot.integrationtests.vo;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.List;

@XmlRootElement
public class PagedModelBook implements Serializable {

    @XmlElement(name = "content")
    private List<BookVO> content;

    public List<BookVO> getContent() {
        return content;
    }

    public void setContent(List<BookVO> content) {
        this.content = content;
    }
}
