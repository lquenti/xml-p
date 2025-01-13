package me.valerius.jaxb.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Lake {
    @XmlAttribute
    private String id;

    @XmlAttribute
    private String country;

    @XmlElement(name = "name")
    private List<String> names = new ArrayList<>();

    @XmlElement(name = "located")
    private List<Located> located = new ArrayList<>();

    @XmlElement(name = "area")
    private String area;

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

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<Located> getLocated() {
        return located;
    }

    public void setLocated(List<Located> located) {
        this.located = located;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
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