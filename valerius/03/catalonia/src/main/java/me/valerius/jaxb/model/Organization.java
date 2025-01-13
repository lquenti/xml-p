package me.valerius.jaxb.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Organization {
    @XmlAttribute
    private String id;

    @XmlAttribute
    private String headq;

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "abbrev")
    private String abbrev;

    @XmlElement(name = "established")
    private String established;

    @XmlElement(name = "members")
    private List<Member> members = new ArrayList<>();

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeadq() {
        return headq;
    }

    public void setHeadq(String headq) {
        this.headq = headq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbrev() {
        return abbrev;
    }

    public void setAbbrev(String abbrev) {
        this.abbrev = abbrev;
    }

    public String getEstablished() {
        return established;
    }

    public void setEstablished(String established) {
        this.established = established;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }
}