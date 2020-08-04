package com.javasolution.app.mentoring.services;

import com.javasolution.app.mentoring.entities.Meeting;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


@Service
@AllArgsConstructor
public class MeetingBookingService {

    private final JavaMailSender javaMailSender;

    private void sendInfoAboutBookedMeeting(String userMail,
                                            Meeting meeting,
                                            String info,
                                            String subject) throws MessagingException {

        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setTo(userMail);
        mimeMessageHelper.setSubject(subject);

        String message = "<head>" +
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
}
