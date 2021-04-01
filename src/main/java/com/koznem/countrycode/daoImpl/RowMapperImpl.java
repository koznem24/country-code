package com.koznem.countrycode.daoImpl;


import com.koznem.countrycode.domain.Domain;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RowMapperImpl implements RowMapper {

    @Override
    public Domain mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        Domain domain = new Domain();

        domain.setName(resultSet.getString("name"));
        domain.setContinent(resultSet.getString("continent"));
        domain.setPopulation(resultSet.getInt("population"));
        domain.setLife_expectancy(resultSet.getDouble("life_expectancy"));
        domain.setCountry_language(resultSet.getString("language"));

        return domain;
    }
}
