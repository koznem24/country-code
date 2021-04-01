package com.koznem.countrycode.domain;

import lombok.Data;

@Data
public class Domain {

    private String name;
    private String continent;
    private Integer population;
    private Double life_expectancy;
    private String country_language;

}
