package me.valerius.jaxb.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Airport {
    @XmlAttribute
    private String iatacode;

    @XmlAttribute
    private String city;

    @XmlAttribute
    private String country;

    @XmlElement(name = "name")
    private List<String> names = new ArrayList<>();

    @XmlElement(name = "latitude")
    private String latitude;

    @XmlElement(name = "longitude")
    private String longitude;

    @XmlElement(name = "elevation")
    private String elevation;

    @XmlElement(name = "gmtOffset")
    private String gmtOffset;

    @XmlElement(name = "located_on")
    private LocatedOn locatedOn;

    // Getters and setters
    public String getIatacode() {
        return iatacode;
    }

    public void setIatacode(String iatacode) {
        this.iatacode = iatacode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getGmtOffset() {
        return gmtOffset;
    }

    public void setGmtOffset(String gmtOffset) {
        this.gmtOffset = gmtOffset;
    }

    public LocatedOn getLocatedOn() {
        return locatedOn;
    }

    public void setLocatedOn(LocatedOn locatedOn) {
        this.locatedOn = locatedOn;
    }
}