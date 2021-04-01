package com.koznem.countrycode.controller;

import com.koznem.countrycode.dao.Dao;
import com.koznem.countrycode.domain.Domain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class Controller {


    public static final String ERROR_MESSAGE = "INVALID_COUNTRY_CODE";
    public static final String ERROR_MESSAGE_CRASH = "INTERNAL_ERROR";

    public static final ResponseEntity<Map<String, Object>>  // To Generate JSON Object when invalid country code entered
            INVALID_COUNTRY_CODE_JSON = new ResponseEntity<>(
            new LinkedHashMap<String, Object>() {{
                put("error message", ERROR_MESSAGE);
            }}, HttpStatus.INTERNAL_SERVER_ERROR);

    public static final ResponseEntity<Map<String, Object>>   // To Generate JSON Object when database server is down
            INTERNAL_ERROR_JSON = new ResponseEntity<>(
            new LinkedHashMap<String, Object>() {{
                put("error message", ERROR_MESSAGE_CRASH);
            }}, HttpStatus.INTERNAL_SERVER_ERROR);

    private Map<String, Object> castToJson(Domain domain1) {   // To Cast returned Object into Json Object

        Map<String, Object> JSON = new LinkedHashMap<>();

        JSON.put("name", domain1.getName());
        System.out.println("Hello");
        JSON.put("continent", domain1.getContinent());
        JSON.put("population", domain1.getPopulation());
        JSON.put("life_expectancy", domain1.getLife_expectancy());
        JSON.put("country_language", domain1.getCountry_language());

        return JSON;
    }

    @Autowired
    Dao domainDao;

    @GetMapping("/{code}")
    public @ResponseBody ResponseEntity<Map<String, Object>> requestedInfo(@PathVariable("code") String code){

        try {
            List<Domain> domains = domainDao.getRequestedInfo(code);

            if(domains.isEmpty())
                return INVALID_COUNTRY_CODE_JSON; // If Country Code is WRONG

            Domain domain1 = domains.get(0);
            Map<String, Object> JSON = castToJson(domain1);

            return new ResponseEntity<>(JSON,HttpStatus.OK); // If Everything is OK
        }catch(Exception e){
            e.getStackTrace();
            return INTERNAL_ERROR_JSON; // If Database or something else is Down
        }

    }


}
