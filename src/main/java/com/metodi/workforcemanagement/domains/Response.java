package com.metodi.workforcemanagement.domains;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "holidays"
})
public class Response {

    @JsonProperty("holidays")
    private List<Holiday> holidays = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    public Response() {
    }

    public Response(List<Holiday> holidays) {
        super();
        this.holidays = holidays;
    }

    @JsonProperty("holidays")
    public List<Holiday> getHolidays() {
        return holidays;
    }

    @JsonProperty("holidays")
    public void setHolidays(List<Holiday> holidays) {
        this.holidays = holidays;
    }

    public Response withHolidays(List<Holiday> holidays) {
        this.holidays = holidays;
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

    public Response withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }
}
