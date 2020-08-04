package com.javasolution.app.mentoring.services;


import com.javasolution.app.mentoring.entities.Meeting;
import com.javasolution.app.mentoring.entities.User;
import com.javasolution.app.mentoring.entities.UserRole;
import com.javasolution.app.mentoring.exceptions.*;
import com.javasolution.app.mentoring.repositories.MeetingRepository;
import com.javasolution.app.mentoring.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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

    protected Meeting findMeeting(String meetingId) {

        final long id;

        try {
            id = Long.parseLong(meetingId);
        } catch (NumberFormatException ex) {
            throw new InvalidCastException("Meeting id have to be long type");
        }

        final Optional<Meeting> meeting = meetingRepository.findById(id);

        return meeting.orElse(null);
    }

    public void deleteMeeting(String meetingId) {

        final Meeting databaseMeeting = findMeeting(meetingId);

        if (databaseMeeting == null)
            throw new MeetingNotFoundException("Meeting with ID: '" + meetingId + "' was not found");

        if (databaseMeeting.getBooked())
            throw new MeetingBookedException("You can not delete meeting with ID: '" + meetingId + "' because someone booked the meeting");

        meetingRepository.deleteById(Long.parseLong(meetingId));
    }

    public Meeting updateMeeting(String meetingId, Meeting meeting) {

        final Meeting databaseMeeting = findMeeting(meetingId);

        if (databaseMeeting == null)
            throw new MeetingNotFoundException("Meeting with ID: '" + meetingId + "' was not found");

        if (databaseMeeting.getBooked())
            throw new MeetingBookedException("You can not update meeting with ID: '" + meetingId + "' because someone booked the meeting");

        meeting.setId(databaseMeeting.getId());
        meeting.setMentor(databaseMeeting.getMentor());
        meeting.setCreateAt(databaseMeeting.getCreateAt());
        meeting.setBooked(databaseMeeting.getBooked());

        final Duration timeBetweenStartMeetingAndEnd = Duration.between(
                meeting.getMeetingStartTime(),
                meeting.getMeetingEndTime()
        );

        final long seconds = timeBetweenStartMeetingAndEnd.getSeconds();

        final int meetingsNumber = (int) seconds / 900;

        if (meetingsNumber != 1)
            throw new MeetingTimeException("Time between meetingStartTime and meetingEndTime have to be 15 minutes");

        //check collisions
        final List<Meeting> collisionMeetings = checkCollision((List<Meeting>) meetingRepository.findAll(), List.of(meeting));

        //there are collisions
        if (!collisionMeetings.isEmpty())
            throw new MeetingsAlreadyExistException("Meetings already exist", collisionMeetings);

        return meetingRepository.save(meeting);
    }

    public Iterable<Meeting> getAllMeetings() {
        return meetingRepository.findAll();
    }

    public Meeting getMeeting(String meetingId) {

        final Meeting meeting = findMeeting(meetingId);

        if (meeting == null)
            throw new MeetingNotFoundException("Meeting with ID: '" + meetingId + "' was not found");

        return meeting;
    }
}
