package com.metodi.workforcemanagement.events.time_off_request_events;

import com.metodi.workforcemanagement.entities.TimeOffRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestCreatedEvent {

    private TimeOffRequest timeOffRequest;

    public RequestCreatedEvent(TimeOffRequest timeOffRequest) {
        this.timeOffRequest = timeOffRequest;
    }
}
