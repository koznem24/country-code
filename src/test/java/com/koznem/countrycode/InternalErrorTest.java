package com.koznem.countrycode;

import com.koznem.countrycode.docker_configuration.DockerProxy;
import com.koznem.countrycode.controller.Controller;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
public class InternalErrorTest {
    @Autowired
    private MockMvc mockMvc;

    @BeforeClass
    public static void setTest() {
        DockerProxy.setTesting(true);
        container.start();
    }

    @Container
    public static PostgreSQLContainer container = new PostgreSQLContainer("postgres:13.2")
            .withUsername("world")
            .withPassword("world123")
            .withDatabaseName("world-db");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url",container::getJdbcUrl);
        registry.add("spring.datasource.password",container::getPassword);
        registry.add("spring.datasource.username",container::getUsername);
    }

    @Test(expected = NullPointerException.class)
    public void dataBaseDownTest() throws Exception {

        String url = "/USA";
        int INTERNAL_SERVER_ERROR_STATUS = 500;


        mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(INTERNAL_SERVER_ERROR_STATUS))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("['error message']")
                        .value(Controller.ERROR_MESSAGE_CRASH));
    }

}
