package com.koznem.countrycode.daoImpl;

import com.koznem.countrycode.dao.Dao;
import com.koznem.countrycode.domain.Domain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DaoImpl implements Dao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<Domain> getRequestedInfo(String country_code) {
            // Joined 2 tables right in the sqlQuery String rather than creating two separate domains
        String sqlQuery = "Select country.name, " +
                "country.continent, country.population, " +
                "country.life_expectancy, country_language.language " +
                "from country join country_language on" +
                " country.code = country_language.country_code" +
                " where code=? AND is_official = 'true';";

        Object[] args = new Object[]{country_code};

        List<Domain> domains = (List<Domain>) jdbcTemplate.query(sqlQuery, new RowMapperImpl(),args);

        return domains;
    }
}
