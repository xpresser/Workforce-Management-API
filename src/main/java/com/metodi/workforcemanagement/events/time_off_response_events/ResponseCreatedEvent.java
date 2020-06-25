package com.metodi.workforcemanagement.events.time_off_response_events;

import com.metodi.workforcemanagement.entities.TimeOffResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseCreatedEvent {
    private TimeOffResponse timeOffResponse;

    public ResponseCreatedEvent(TimeOffResponse timeOffResponse) {
        this.timeOffResponse = timeOffResponse;
    }
}
