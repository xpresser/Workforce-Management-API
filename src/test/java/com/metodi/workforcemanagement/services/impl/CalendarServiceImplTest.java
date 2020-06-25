package com.metodi.workforcemanagement.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metodi.workforcemanagement.domains.CalendarificAPI;
import com.metodi.workforcemanagement.services.CalendarService;
import com.metodi.workforcemanagement.test_resources.CalendarServiceTestJSON;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class CalendarServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Autowired
    private CalendarService calendarService;

    private CalendarificAPI calendarificAPI;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        String json = CalendarServiceTestJSON.HOLIDAYS_API_MOCK_RESULT;
        calendarificAPI = new ObjectMapper().readValue(json, CalendarificAPI.class);
    }

    @Test
    void getNumberOfWorkingDays() {
        LocalDate startDate = LocalDate.parse("2020-05-22");
        LocalDate endDate = LocalDate.parse("2020-05-28");

        Map<String, String> vars = new HashMap<>();
        vars.put("api_key", "3784e81bed94d7044cab0fc981c7aa1af0e59e4c");
        vars.put("country", "BG");
        vars.put("year", "2020");

        Mockito
                .when(restTemplate.getForObject("https://calendarific.com/api/v2/holidays?&api_key={api_key}&country={country}&year={year}", CalendarificAPI.class, vars))
          .thenReturn(calendarificAPI);

        int result = calendarService.getNumberOfWorkingDays(startDate, endDate);

        assertAll(
                () -> assertEquals(4, result)
        );
    }
}