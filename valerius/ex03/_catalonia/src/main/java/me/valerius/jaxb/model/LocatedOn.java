package me.valerius.jaxb.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class LocatedOn {
    @XmlAttribute
    private String island;

    // Getters and setters
    public String getIsland() {
        return island;
    }

    public void setIsland(String island) {
        this.island = island;
    }
}