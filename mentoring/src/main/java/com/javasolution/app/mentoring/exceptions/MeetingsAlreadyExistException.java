package com.javasolution.app.mentoring.exceptions;

import com.javasolution.app.mentoring.entities.Meeting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
@Setter
public class MeetingsAlreadyExistException extends RuntimeException {

    final List<MeetingModel> collisionMeetings;

    @AllArgsConstructor
    @Getter
    @Setter
    private class MeetingModel {
        private LocalDate meetingDate;
        private LocalTime meetingStartTime;
        private LocalTime meetingEndTime;
    }

    public MeetingsAlreadyExistException(final String message, final List<Meeting> collisionMeetings) {
        super(message);

        this.collisionMeetings = new ArrayList<>();

        for (Meeting collisionMeeting : collisionMeetings) {

            this.collisionMeetings.add(
                    new MeetingModel(
                            collisionMeeting.getMeetingDate(),
                            collisionMeeting.getMeetingStartTime(),
                            collisionMeeting.getMeetingEndTime()
                    )
            );
        }
    }
}
