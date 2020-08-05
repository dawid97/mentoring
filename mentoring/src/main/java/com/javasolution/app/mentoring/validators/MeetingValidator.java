package com.javasolution.app.mentoring.validators;

import com.javasolution.app.mentoring.entities.Meeting;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;


@Component
public class MeetingValidator implements Validator {

    @Override
    public boolean supports(final Class<?> aClass) {
        return Meeting.class.equals(aClass);
    }

    @Override
    public void validate(final Object object, final Errors errors) {
        final Meeting meeting = (Meeting) object;

        if (meeting.getMeetingDate().isBefore(LocalDate.now()))
            errors.rejectValue("meetingDate",
                    "Wrong date",
                    "The date cannot be in the past");

        if (meeting.getMeetingEndTime().isBefore(meeting.getMeetingStartTime()))
            errors.rejectValue("meetingEndTime",
                    "Wrong time",
                    "meetingEndTime cannot be less than meetingStartTime");

        if (meeting.getMeetingStartTime().equals(meeting.getMeetingEndTime()))
            errors.rejectValue("meetingStartTime",
                    "Wrong time",
                    "meetingStartTime and meetingEndTime cannot be the same");

        final int startMinutes = meeting.getMeetingStartTime().getMinute();
        if (startMinutes != 0 && startMinutes != 15 && startMinutes != 30 && startMinutes != 45)
            errors.rejectValue("meetingStartTime",
                    "Wrong time",
                    "Use one of the given minute values: 0,15,30,45");

        final int endMinutes = meeting.getMeetingEndTime().getMinute();
        if (endMinutes != 0 && endMinutes != 15 && endMinutes != 30 && endMinutes != 45)
            errors.rejectValue("meetingEndTime",
                    "Wrong time",
                    "Use one of the given minute values: 0,15,30,45");
    }
}
