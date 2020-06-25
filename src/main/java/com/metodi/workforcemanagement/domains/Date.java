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
        "iso",
        "datetime"
})
public class Date {

    @JsonProperty("iso")
    private String iso;
    @JsonProperty("datetime")
    private Datetime datetime;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    public Date() {
    }

    public Date(String iso, Datetime datetime) {
        super();
        this.iso = iso;
        this.datetime = datetime;
    }

    @JsonProperty("iso")
    public String getIso() {
        return iso;
    }

    @JsonProperty("iso")
    public void setIso(String iso) {
        this.iso = iso;
    }

    public Date withIso(String iso) {
        this.iso = iso;
        return this;
    }

    @JsonProperty("datetime")
    public Datetime getDatetime() {
        return datetime;
    }

    @JsonProperty("datetime")
    public void setDatetime(Datetime datetime) {
        this.datetime = datetime;
    }

    public Date withDatetime(Datetime datetime) {
        this.datetime = datetime;
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

    public Date withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }
}

