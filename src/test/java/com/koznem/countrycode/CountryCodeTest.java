package com.koznem.countrycode;

import com.koznem.countrycode.docker_configuration.DockerInitializer;
import com.koznem.countrycode.controller.Controller;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CountryCodeTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void incorrectCountryCodeTest()
            throws Exception {
        while (!DockerInitializer.isReady()) {}
        Thread.sleep(5000);

        //given
        String url = "/SSS"; // This url is originally INVALID
        int INTERNAL_SERVER_ERROR_STATUS = 500;

        //when, then
        mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(INTERNAL_SERVER_ERROR_STATUS))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("['error message']")
                        .value(Controller.ERROR_MESSAGE));
    }
}
