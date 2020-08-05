package com.javasolution.app.mentoring.services;

import com.javasolution.app.mentoring.entities.Meeting;
import com.javasolution.app.mentoring.entities.MeetingBooking;
import com.javasolution.app.mentoring.entities.User;
import com.javasolution.app.mentoring.entities.UserRole;
import com.javasolution.app.mentoring.exceptions.*;
import com.javasolution.app.mentoring.repositories.MeetingBookingRepository;
import com.javasolution.app.mentoring.repositories.MeetingRepository;
import com.javasolution.app.mentoring.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.security.Principal;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class MeetingBookingService {

    private final MeetingService meetingService;
    private final UserRepository userRepository;
    private final MeetingBookingRepository meetingBookingRepository;
    private final JavaMailSender javaMailSender;
    private final MeetingRepository meetingRepository;

    private void sendInfoAboutBookedMeeting(final String userMail,
                                            final Meeting meeting,
                                            final String info,
                                            final String subject) throws MessagingException {

        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setTo(userMail);
        mimeMessageHelper.setSubject(subject);

        final String message = "<head>" +
                "<style type=\"text/css\">" +
                ".red { color: #f00; }" +
                "</style>" +
                "</head>" +
                "<h1 class=\"red\">" + info + "\uD83D\uDE00</h1>" +
                "<div>" +
                "<b>Meeting date:</b>" + meeting.getMeetingDate() +
                "</div>" +
                "<div>" +
                "<b>Meeting time:</b>" + meeting.getMeetingStartTime() + "-" + meeting.getMeetingEndTime() +
                "</div>";

        mimeMessage.setContent(message, "text/html; charset=utf-8");
        javaMailSender.send(mimeMessage);
    }

    public MeetingBooking bookingMeeting(final String meetingId, final Principal principal) {

        //check if meeting exists
        final Meeting meeting = meetingService.findMeeting(meetingId);
        if (meeting == null)
            throw new MeetingNotFoundException("Meeting with ID: '" + meetingId + "' was not found");
        else
            meeting.setBooked(true);

        //check if meeting booked
        final Optional<MeetingBooking> databaseMeetingBooking = meetingBookingRepository.findByMeetingId(meeting.getId());
        if (databaseMeetingBooking.isPresent())
            throw new MeetingBookingAlreadyExistsException("Meeting with ID: '" + meetingId + "' is already booked");

        //find student
        final User student = userRepository.findByEmail(principal.getName());

        //create meetingBooking
        final MeetingBooking meetingBooking = new MeetingBooking();
        meetingBooking.setMeeting(meeting);
        meetingBooking.setStudent(student);


        //check if mentor exists
        final User mentor = userRepository.findByUserRole(UserRole.MENTOR);
        if (mentor == null) throw new MentorNotFoundException("Mentor can not be found");

        try {

            //send email to student
            sendInfoAboutBookedMeeting(principal.getName(),
                    meeting,
                    "Thank you for booking meeting!",
                    "Booked meeting!");

            //send email to mentor
            sendInfoAboutBookedMeeting(mentor.getEmail(),
                    meeting,
                    "Student: " + principal.getName() + " booked meeting!",
                    "Booked meeting!");

        } catch (MessagingException ex) {
            throw new UnableSendEmailException("Something went wrong. Please try again later");
        }

        meetingRepository.save(meeting);
        return meetingBookingRepository.save(meetingBooking);
    }

    protected MeetingBooking findMeetingBooking(final String bookingId) {

        final long id;

        try {
            id = Long.parseLong(bookingId);
        } catch (NumberFormatException ex) {
            throw new InvalidCastException("Meeting booking id have to be long type");
        }

        final Optional<MeetingBooking> meetingBooking = meetingBookingRepository.findById(id);

        return meetingBooking.orElse(null);
    }

    public void cancelBooking(final String bookingId, final Principal principal) {

        //check if meeting booking exists
        final MeetingBooking meetingBooking = findMeetingBooking(bookingId);
        if (meetingBooking == null)
            throw new MeetingBookingNotFoundException("Meeting booking with ID: '" + bookingId + "' was not found");

        final User student = userRepository.findByEmail(principal.getName());

        //check if owner
        if (!meetingBooking.getStudent().getId().equals(student.getId()))
            throw new NotOwnerException("You are not owner the meeting booking");

        //check if mentor exists
        final User mentor = userRepository.findByUserRole(UserRole.MENTOR);
        if (mentor == null) throw new MentorNotFoundException("Mentor can not be found");

        final Meeting meeting = meetingBooking.getMeeting();
        meeting.setBooked(false);

        try {

            //send email to student
            sendInfoAboutBookedMeeting(principal.getName(),
                    meeting,
                    "You have successfully canceled the meeting!",
                    "Cancellation of the meeting!");

            //send email to mentor
            sendInfoAboutBookedMeeting(mentor.getEmail(),
                    meeting,
                    "Student: " + principal.getName() + " canceled the meeting!",
                    "Cancellation of the meeting!");

        } catch (final MessagingException ex) {
            throw new UnableSendEmailException("Something went wrong. Please try again later");
        }

        meetingRepository.save(meeting);
        meetingBookingRepository.delete(meetingBooking);
    }

    public Iterable<MeetingBooking> getAllBookings() {
        return meetingBookingRepository.findAll();
    }

    public MeetingBooking getBooking(final String bookingId) {

        final MeetingBooking meetingBooking = findMeetingBooking(bookingId);

        if (meetingBooking == null)
            throw new MeetingBookingNotFoundException("Meeting booking with ID: '" + bookingId + "' was not found");

        return meetingBooking;
    }

    public MeetingBooking getMyBooking(final String bookingId, final Principal principal) {

        final MeetingBooking meetingBooking = findMeetingBooking(bookingId);

        if (meetingBooking == null)
            throw new MeetingBookingNotFoundException("Meeting booking with ID: '" + bookingId + "' was not found");

        if (!meetingBooking.getStudent().getEmail().equals(principal.getName()))
            throw new NotOwnerException("You are not owner the meeting booking");

        return meetingBooking;
    }

    public Iterable<MeetingBooking> getAllMyBookings(final Principal principal) {

        final User student = userRepository.findByEmail(principal.getName());

        return meetingBookingRepository.findAllByStudent(student);
    }
}
