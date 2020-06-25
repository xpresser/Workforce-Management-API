package com.metodi.workforcemanagement.services.impl;

import com.metodi.workforcemanagement.domains.CalendarificAPI;
import com.metodi.workforcemanagement.services.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class CalendarServiceImpl implements CalendarService {
    private final RestTemplate restTemplate;
    @Value("${calendarific.api_key}")
    private String api_key;
    @Value("${calendarific.country}")
    private String country;
    @Value("${calendarific.url}")
    private String url;
    private List<LocalDate> holidayDates = new ArrayList<>();
    private int year = Calendar.getInstance().get(Calendar.YEAR);

    @Autowired
    public CalendarServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    private void init() {
        Map<String, String> vars = new HashMap<>();
        vars.put("api_key", api_key);
        vars.put("country", country);
        vars.put("year", year+"");

        CalendarificAPI holidaysResult =  restTemplate.getForObject(url, CalendarificAPI.class, vars);
        stringToLocalDate(holidaysResult);
    }

    private List<String> getDatesStringsFromResult(CalendarificAPI holidaysResult) {
        List<String> dates = new ArrayList<>();
        List<String> typeNationalHoliday = new ArrayList<>();
        typeNationalHoliday.add("National holiday");

        holidaysResult.getResponse().getHolidays().stream()
                .filter(holiday -> holiday.getType().equals(typeNationalHoliday))
                .forEach(holiday -> dates.add(holiday.getDate().getIso()));
        return dates;
    }

    private void stringToLocalDate(CalendarificAPI holidaysResult) {
        getDatesStringsFromResult(holidaysResult).forEach(string -> holidayDates.add(LocalDate.parse(string)));
    }

    @Override
    public Integer getNumberOfWorkingDays (LocalDate startDate, LocalDate endDate) {
        if (Calendar.getInstance().get(Calendar.YEAR) != this.year) {
            this.year = Calendar.getInstance().get(Calendar.YEAR);
            init();
        }
        return (int) startDate.datesUntil(endDate.plus(1,ChronoUnit.DAYS))
                .filter(t -> List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY")
                        .contains(t.getDayOfWeek().name()))
                        .filter(t -> !holidayDates.contains(t))
                        .count();
    }
}
