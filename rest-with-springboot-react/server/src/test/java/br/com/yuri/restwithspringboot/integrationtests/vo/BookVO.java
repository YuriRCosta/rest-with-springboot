package br.com.yuri.restwithspringboot.integrationtests.vo;

import jakarta.xml.bind.annotation.XmlRootElement;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@XmlRootElement
public class BookVO extends RepresentationModel<BookVO> implements Serializable {

    private Long id;

    private String author;
    private String title;
    private Date launchDate;
    private Double price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BookVO bookVO = (BookVO) o;
        return Objects.equals(id, bookVO.id) && Objects.equals(author, bookVO.author) && Objects.equals(title, bookVO.title) && Objects.equals(launchDate, bookVO.launchDate) && Objects.equals(price, bookVO.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, author, title, launchDate, price);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(Date launchDate) {
        this.launchDate = launchDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}

