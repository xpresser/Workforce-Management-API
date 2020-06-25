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
        "name",
        "description",
        "date",
        "type"
})
public class Holiday {

    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("date")
    private Date date;
    @JsonProperty("type")
    private List<String> type = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    public Holiday() {
    }

    public Holiday(String name, String description, Date date, List<String> type) {
        super();
        this.name = name;
        this.description = description;
        this.date = date;
        this.type = type;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public Holiday withName(String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    public Holiday withDescription(String description) {
        this.description = description;
        return this;
    }

    @JsonProperty("date")
    public Date getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(Date date) {
        this.date = date;
    }

    public Holiday withDate(Date date) {
        this.date = date;
        return this;
    }

    @JsonProperty("type")
    public List<String> getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(List<String> type) {
        this.type = type;
    }

    public Holiday withType(List<String> type) {
        this.type = type;
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

    public Holiday withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }
}
