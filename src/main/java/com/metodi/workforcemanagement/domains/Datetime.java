package com.metodi.workforcemanagement.domains;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "year",
        "month",
        "day"
})
public class Datetime {

    @JsonProperty("year")
    private Integer year;
    @JsonProperty("month")
    private Integer month;
    @JsonProperty("day")
    private Integer day;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    public Datetime() {
    }

    public Datetime(Integer year, Integer month, Integer day) {
        super();
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @JsonProperty("year")
    public Integer getYear() {
        return year;
    }

    @JsonProperty("year")
    public void setYear(Integer year) {
        this.year = year;
    }

    public Datetime withYear(Integer year) {
        this.year = year;
        return this;
    }

    @JsonProperty("month")
    public Integer getMonth() {
        return month;
    }

    @JsonProperty("month")
    public void setMonth(Integer month) {
        this.month = month;
    }

    public Datetime withMonth(Integer month) {
        this.month = month;
        return this;
    }

    @JsonProperty("day")
    public Integer getDay() {
        return day;
    }

    @JsonProperty("day")
    public void setDay(Integer day) {
        this.day = day;
    }

    public Datetime withDay(Integer day) {
        this.day = day;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Datetime withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }
}
