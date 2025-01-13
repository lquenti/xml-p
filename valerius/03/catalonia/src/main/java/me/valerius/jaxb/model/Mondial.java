package me.valerius.jaxb.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "mondial")
@XmlAccessorType(XmlAccessType.FIELD)
public class Mondial {
    @XmlElement(name = "country")
    private List<Country> countries = new ArrayList<>();

    @XmlElement(name = "continent")
    private List<Continent> continents = new ArrayList<>();

    @XmlElement(name = "organization")
    private List<Organization> organizations = new ArrayList<>();

    @XmlElement(name = "sea")
    private List<Sea> seas = new ArrayList<>();

    @XmlElement(name = "river")
    private List<River> rivers = new ArrayList<>();

    @XmlElement(name = "lake")
    private List<Lake> lakes = new ArrayList<>();

    @XmlElement(name = "island")
    private List<Island> islands = new ArrayList<>();

    @XmlElement(name = "mountain")
    private List<Mountain> mountains = new ArrayList<>();

    @XmlElement(name = "desert")
    private List<Desert> deserts = new ArrayList<>();

    @XmlElement(name = "airport")
    private List<Airport> airports = new ArrayList<>();

    @XmlElement(name = "langtree")
    private List<LangTree> langTrees = new ArrayList<>();

    @XmlElement(name = "city")
    private List<City> cities = new ArrayList<>();

    // Getters and setters
    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

    public List<Continent> getContinents() {
        return continents;
    }

    public void setContinents(List<Continent> continents) {
        this.continents = continents;
    }

    public List<Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<Organization> organizations) {
        this.organizations = organizations;
    }

    public List<Sea> getSeas() {
        return seas;
    }

    public void setSeas(List<Sea> seas) {
        this.seas = seas;
    }

    public List<River> getRivers() {
        return rivers;
    }

    public void setRivers(List<River> rivers) {
        this.rivers = rivers;
    }

    public List<Lake> getLakes() {
        return lakes;
    }

    public void setLakes(List<Lake> lakes) {
        this.lakes = lakes;
    }

    public List<Island> getIslands() {
        return islands;
    }

    public void setIslands(List<Island> islands) {
        this.islands = islands;
    }

    public List<Mountain> getMountains() {
        return mountains;
    }

    public void setMountains(List<Mountain> mountains) {
        this.mountains = mountains;
    }

    public List<Desert> getDeserts() {
        return deserts;
    }

    public void setDeserts(List<Desert> deserts) {
        this.deserts = deserts;
    }

    public List<Airport> getAirports() {
        return airports;
    }

    public void setAirports(List<Airport> airports) {
        this.airports = airports;
    }

    public List<LangTree> getLangTrees() {
        return langTrees;
    }

    public void setLangTrees(List<LangTree> langTrees) {
        this.langTrees = langTrees;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }
}