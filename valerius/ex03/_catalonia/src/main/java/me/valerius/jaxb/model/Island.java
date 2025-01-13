package me.valerius.jaxb.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Island {
    @XmlAttribute
    private String id;

    @XmlAttribute
    private String sea;

    @XmlAttribute
    private String lake;

    @XmlAttribute
    private String river;

    @XmlAttribute
    private String country;

    @XmlAttribute
    private String type;

    @XmlElement(name = "name")
    private List<String> names = new ArrayList<>();

    @XmlElement(name = "localname")
    private String localName;

    @XmlElement(name = "islands")
    private String islands;

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

    public String getSea() {
        return sea;
    }

    public void setSea(String sea) {
        this.sea = sea;
    }

    public String getLake() {
        return lake;
    }

    public void setLake(String lake) {
        this.lake = lake;
    }

    public String getRiver() {
        return river;
    }

    public void setRiver(String river) {
        this.river = river;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getIslands() {
        return islands;
    }

    public void setIslands(String islands) {
        this.islands = islands;
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