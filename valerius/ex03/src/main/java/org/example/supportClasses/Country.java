package org.example.supportClasses;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the database model of a country
 * defined in mondial.dtd.
 */
@Setter
@Getter
public class Country {
    private String carCode;
    private String name;
    private String localName;
    private List<Population> populations = new ArrayList<>();
    private Double populationGrowth;
    private Double infantMortality;
    private Double gdpTotal;
    private Double gdpAgri;
    private Double gdpInd;
    private Double gdpServ;
    private Double inflation;
    private Double unemployment;
    private String indepDate;
    private String indepFrom;
    private String government;
    private List<Encompassed> encompassed = new ArrayList<>();
    private List<EthnicGroup> ethnicGroups = new ArrayList<>();
    private List<Religion> religions = new ArrayList<>();
    private List<Language> languages = new ArrayList<>();
    private List<Border> borders = new ArrayList<>();
    private List<Province> provinces = new ArrayList<>();
    private List<City> cities = new ArrayList<>();
}
