package me.valerius.jaxb.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class LangTree {
    @XmlAttribute
    private String country;

    @XmlElement(name = "name")
    private List<String> names = new ArrayList<>();

    @XmlElement(name = "spokenby")
    private List<SpokenBy> spokenBy = new ArrayList<>();

    @XmlElement(name = "langtree")
    private List<LangTree> langTrees = new ArrayList<>();

    // Getters and setters
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<SpokenBy> getSpokenBy() {
        return spokenBy;
    }

    public void setSpokenBy(List<SpokenBy> spokenBy) {
        this.spokenBy = spokenBy;
    }

    public List<LangTree> getLangTrees() {
        return langTrees;
    }

    public void setLangTrees(List<LangTree> langTrees) {
        this.langTrees = langTrees;
    }
}