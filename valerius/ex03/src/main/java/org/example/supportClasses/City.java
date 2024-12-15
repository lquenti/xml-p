package org.example.supportClasses;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class City {
    private String id;
    private String country;
    private String province;
    private List<String> names;
    private String localName;
    private Double latitude;
    private Double longitude;
    private Double elevation;
    private List<Population> population;
    private List<LocatedAt> locatedAt;
    private List<LocatedOn> locatedOn;

    @Getter
    @Setter
    @ToString
    public static class Population {
        private int value;
        private int year;
        private String measured;
    }

    @Getter
    @Setter
    @ToString
    public static class LocatedAt {
        // TODO: use enum
        private String waterType; // can be "river", "sea", or "lake"
        private List<String> rivers;
        private List<String> seas;
        private List<String> lakes;
    }

    @Getter
    @Setter
    @ToString
    public static class LocatedOn {
        private String island;
    }
}
