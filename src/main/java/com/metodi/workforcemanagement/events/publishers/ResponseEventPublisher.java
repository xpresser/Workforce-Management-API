package com.metodi.workforcemanagement.events.publishers;

import com.metodi.workforcemanagement.entities.TimeOffResponse;
import com.metodi.workforcemanagement.events.time_off_response_events.ResponseCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ResponseEventPublisher {
    private final ApplicationEventPublisher publisher;

    ResponseEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishResponseCreatedEvent(final TimeOffResponse timeOffResponse) {
        publisher.publishEvent(new ResponseCreatedEvent(timeOffResponse));
    }
}
