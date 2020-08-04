package com.javasolution.app.mentoring.services;


import com.javasolution.app.mentoring.entities.Meeting;
import com.javasolution.app.mentoring.entities.User;
import com.javasolution.app.mentoring.entities.UserRole;
import com.javasolution.app.mentoring.exceptions.MeetingsAlreadyExistException;
import com.javasolution.app.mentoring.exceptions.MentorNotFoundException;
import com.javasolution.app.mentoring.repositories.MeetingRepository;
import com.javasolution.app.mentoring.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;

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

    public List<Meeting> addMeeting(Meeting meeting) {

        final Duration timeBetweenStartMeetingAndEnd = Duration.between(
                meeting.getMeetingStartTime(),
                meeting.getMeetingEndTime()
        );

        final long seconds = timeBetweenStartMeetingAndEnd.getSeconds();
        final List<Meeting> meetings = new ArrayList();
        final int meetingsNumber = (int) seconds / 900;

        //check if a mentor exists
        final User mentor = userRepository.findByUserRole(UserRole.MENTOR);
        if (mentor == null) throw new MentorNotFoundException("Mentor can not be found");

        //division into quarters of hour
        for (int i = 0; i < meetingsNumber; i++) {
            final Meeting newMeeting = new Meeting();
            newMeeting.setMeetingDate(meeting.getMeetingDate());
            newMeeting.setMeetingStartTime(meeting.getMeetingStartTime().plusMinutes(15 * i));
            newMeeting.setMeetingEndTime(newMeeting.getMeetingStartTime().plusMinutes(15));
            newMeeting.setMentor(mentor);
            meetings.add(newMeeting);
        }

        //check collisions
        final List<Meeting> collisionMeetings = checkCollision((List<Meeting>) meetingRepository.findAll(), meetings);

        //there are collisions
        if (!collisionMeetings.isEmpty())
            throw new MeetingsAlreadyExistException("Meetings already exist", collisionMeetings);

        //there are no collisions
        for (Meeting meetingToSave : meetings)
            meetingRepository.save(meetingToSave);

        return meetings;
    }
}
