package me.valerius.jaxb.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class River {
    @XmlAttribute
    private String id;

    @XmlAttribute
    private String country;

    @XmlElement(name = "name")
    private List<String> names = new ArrayList<>();

    @XmlElement(name = "located")
    private List<Located> located = new ArrayList<>();

    @XmlElement(name = "source")
    private Source source;

    @XmlElement(name = "estuary")
    private Estuary estuary;

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<Located> getLocated() {
        return located;
    }

    public void setLocated(List<Located> located) {
        this.located = located;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Estuary getEstuary() {
        return estuary;
    }

    public void setEstuary(Estuary estuary) {
        this.estuary = estuary;
    }
}