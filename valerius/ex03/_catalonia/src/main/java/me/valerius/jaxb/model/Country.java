package me.valerius.jaxb.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "country")
@XmlAccessorType(XmlAccessType.FIELD)
public class Country {
    @XmlAttribute(name = "car_code")
    private String carCode;

    @XmlAttribute
    private String area;

    @XmlAttribute
    private String capital;

    @XmlAttribute
    private String memberships;

    @XmlElement(name = "name")
    private List<String> names = new ArrayList<>();

    @XmlElement(name = "localname")
    private String localName;

    @XmlElement(name = "population")
    private List<Population> populations = new ArrayList<>();

    @XmlElement(name = "encompassed")
    private List<Encompassed> encompassed = new ArrayList<>();

    @XmlElement(name = "ethnicgroup")
    private List<PercentageProperty> ethnicGroups = new ArrayList<>();

    @XmlElement(name = "religion")
    private List<PercentageProperty> religions = new ArrayList<>();

    @XmlElement(name = "language")
    private List<PercentageProperty> languages = new ArrayList<>();

    @XmlElement(name = "border")
    private List<Border> borders = new ArrayList<>();

    @XmlElement(name = "province")
    private List<Province> provinces = new ArrayList<>();

    // Getters and setters
    public String getCarCode() {
        return carCode;
    }

    public void setCarCode(String carCode) {
        this.carCode = carCode;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getMemberships() {
        return memberships;
    }

    public void setMemberships(String memberships) {
        this.memberships = memberships;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<Encompassed> getEncompassed() {
        return encompassed;
    }

    public void setEncompassed(List<Encompassed> encompassed) {
        this.encompassed = encompassed;
    }

    public List<PercentageProperty> getEthnicGroups() {
        return ethnicGroups;
    }

    public void setEthnicGroups(List<PercentageProperty> ethnicGroups) {
        this.ethnicGroups = ethnicGroups;
    }

    public List<PercentageProperty> getReligions() {
        return religions;
    }

    public void setReligions(List<PercentageProperty> religions) {
        this.religions = religions;
    }

    public List<PercentageProperty> getLanguages() {
        return languages;
    }

    public void setLanguages(List<PercentageProperty> languages) {
        this.languages = languages;
    }

    public List<Province> getProvinces() {
        return provinces;
    }

    public void setProvinces(List<Province> provinces) {
        this.provinces = provinces;
    }
}