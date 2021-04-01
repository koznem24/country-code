package com.koznem.countrycode.dao;

import com.koznem.countrycode.domain.Domain;

import java.util.List;


public interface Dao {

    List<Domain> getRequestedInfo(String country_code); // To Request data from DB
}
