package org.example.supportClasses;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the database model of a country
 * defined in mondial.dtd.
 */
@Getter
@Setter
@ToString
public class Country {
    private String carCode;
    private String name;
    private String localName;
    private double area;
    private String capital;
    private String memberships;
    private String indepDate = "";
    private String indepDateFrom = "";
    private List<Population> populations = new ArrayList<>();
    private List<EthnicGroup> ethnicGroups = new ArrayList<>();
    private List<Religion> religions = new ArrayList<>();
    private List<Language> languages = new ArrayList<>();
    private List<Border> borders = new ArrayList<>();
    private List<Province> provinces = new ArrayList<>();
    private List<City> cities = new ArrayList<>();


    private double populationGrowth;
    private double infantMortality;
    private double gdpTotal;
    private double gdpAgri;
    private double gdpInd;
    private double gdpServ;
    private double inflation;
    private double unemployment;
    private String government;
    private List<Encompassed> encompassed = new ArrayList<>();
}
