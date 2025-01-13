package me.valerius.jaxb.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Mountain {
    @XmlAttribute
    private String id;

    @XmlAttribute
    private String country;

    @XmlAttribute
    private String island;

    @XmlAttribute
    private String type;

    @XmlAttribute(name = "last_eruption")
    private String lastEruption;

    @XmlElement(name = "name")
    private List<String> names = new ArrayList<>();

    @XmlElement(name = "mountains")
    private String mountains;

    @XmlElement(name = "located")
    private List<Located> located = new ArrayList<>();

    @XmlElement(name = "latitude")
    private String latitude;

    @XmlElement(name = "longitude")
    private String longitude;

    @XmlElement(name = "elevation")
    private String elevation;

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

    public String getIsland() {
        return island;
    }

    public void setIsland(String island) {
        this.island = island;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLastEruption() {
        return lastEruption;
    }

    public void setLastEruption(String lastEruption) {
        this.lastEruption = lastEruption;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public String getMountains() {
        return mountains;
    }

    public void setMountains(String mountains) {
        this.mountains = mountains;
    }

    public List<Located> getLocated() {
        return located;
    }

    public void setLocated(List<Located> located) {
        this.located = located;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getElevation() {
        return elevation;
    }

    public void setElevation(String elevation) {
        this.elevation = elevation;
    }
}