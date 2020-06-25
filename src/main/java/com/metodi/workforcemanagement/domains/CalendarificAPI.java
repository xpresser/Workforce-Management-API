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
        "meta",
        "response"
})
public class CalendarificAPI {
    @JsonProperty("meta")
    private Meta meta;
    @JsonProperty("response")
    private Response response;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    public CalendarificAPI() {
    }

    public CalendarificAPI(Meta meta, Response response) {
        super();
        this.meta = meta;
        this.response = response;
    }

    @JsonProperty("meta")
    public Meta getMeta() {
        return meta;
    }

    @JsonProperty("meta")
    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public CalendarificAPI withMeta(Meta meta) {
        this.meta = meta;
        return this;
    }

    @JsonProperty("response")
    public Response getResponse() {
        return response;
    }

    @JsonProperty("response")
    public void setResponse(Response response) {
        this.response = response;
    }

    public CalendarificAPI withResponse(Response response) {
        this.response = response;
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

    public CalendarificAPI withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }
}
