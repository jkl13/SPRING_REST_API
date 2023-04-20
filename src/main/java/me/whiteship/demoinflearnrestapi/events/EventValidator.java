package me.whiteship.demoinflearnrestapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {
    
    public void validate(EventDto eventDto, Errors errors) {
        if(eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() > 0) {
            errors.rejectValue("basePrice", "wrongValue", "basePrice is Wrong.");
            errors.rejectValue("maxPrice", "wrongValue", "maxPrice is Wrong.");
        }

        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if(endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
            endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
            endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())
        ) {
            errors.rejectValue("endEventDateTime", "wrongValue", "maxPrice is Wrong.");
        }

        //TODO beginEventDateTime
        LocalDateTime beginEventDateTime = eventDto.getBeginEventDateTime();
        if (beginEventDateTime.isAfter(eventDto.getEndEventDateTime()) ||
            beginEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
            beginEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())
        ) {
            errors.rejectValue("beginEventDateTime", "wrongValue", "beginEvenDateTime is wrong");
        }

        //TODO closeEnrollmentDateTime
        LocalDateTime closeEnrollmentDateTime = eventDto.getCloseEnrollmentDateTime();
        if (closeEnrollmentDateTime.isAfter(eventDto.getBeginEventDateTime()) ||
            closeEnrollmentDateTime.isAfter(eventDto.getEndEventDateTime()) ||
            closeEnrollmentDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())
        ) {
            errors.rejectValue("closeEnrollmentDateTime", "wrongValue", "closeEnrollmentDateTime is wrong");
        }
    }
    
}
