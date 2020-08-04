package com.javasolution.app.mentoring.services;


import com.javasolution.app.mentoring.entities.Meeting;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
public class MeetingService {

    private List<Meeting> checkCollision(List<Meeting> databaseMeetings, List<Meeting> meetings) {
        final List<Meeting> collisionMeetings = new ArrayList<>();

        for (Meeting meeting : meetings) {
            for (Meeting databaseMeeting : databaseMeetings) {

                if (meeting.getMeetingStartTime().equals(databaseMeeting.getMeetingStartTime())
                        && meeting.getMeetingEndTime().equals(databaseMeeting.getMeetingEndTime())
                        && meeting.getMeetingDate().equals(databaseMeeting.getMeetingDate())) {
                    collisionMeetings.add(meeting);
                }
            }
        }

        return collisionMeetings;
    }
}
