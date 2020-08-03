package com.javasolution.app.mentoring.services;

import com.javasolution.app.mentoring.entities.*;
import com.javasolution.app.mentoring.exceptions.*;
import com.javasolution.app.mentoring.repositories.MeetingBookingRepository;
import com.javasolution.app.mentoring.repositories.MeetingRepository;
import com.javasolution.app.mentoring.repositories.UserRepository;
import com.javasolution.app.mentoring.requests.UpdateUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired //must be autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private final UserRepository userRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final JavaMailSender javaMailSender;
    private final MeetingRepository meetingRepository;
    private final MeetingBookingRepository meetingBookingRepository;

    public UserService(UserRepository userRepository,
                       ConfirmationTokenService confirmationTokenService,
                       JavaMailSender javaMailSender,
                       MeetingRepository meetingRepository,
                       MeetingBookingRepository meetingBookingRepository) {
        this.confirmationTokenService = confirmationTokenService;
        this.userRepository = userRepository;
        this.javaMailSender = javaMailSender;
        this.meetingRepository = meetingRepository;
        this.meetingBookingRepository = meetingBookingRepository;
    }

    void sendConfirmationMail(String userMail, String token) throws MessagingException {

        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setTo(userMail);
        mimeMessageHelper.setSubject("Mail Confirmation Link!");

        String message = "<head>" +
                "<style type=\"text/css\">" +
                ".red { color: #f00; }" +
                "</style>" +
                "</head>" +
                "<h1 class=\"red\">Thank you for registering! \uD83D\uDE00</h1>" +
                "<p>" +
                "Please click on the below link to activate your account." +
                "</p>" +
                "<div>" +
                "http://localhost:8080/api/users/sign-up/confirm?token=" + token +
                "</div>";

        mimeMessage.setContent(message, "text/html; charset=utf-8");
        javaMailSender.send(mimeMessage);
    }

    public User updateMe(UpdateUserRequest updateUserRequest, Principal principal) {

        final User user = userRepository.findByEmail(principal.getName());

        user.setName(updateUserRequest.getName());
        user.setSurname(updateUserRequest.getSurname());

        return userRepository.save(user);
    }

    protected User findUser(String userId) {

        final long id;

        try {
            id = Long.parseLong(userId);
        } catch (NumberFormatException ex) {
            throw new InvalidCastException("User id have to be long type");
        }

        final Optional<User> user = userRepository.findById(id);

        return user.orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        final User user = userRepository.findByEmail(email);

        if (user == null)
            throw new UsernameNotFoundException("User with email " + email + " cannot be found.");
        else
            return user;
    }

    public void deleteUser(String userId) {

        final User student = findUser(userId);

        if (student == null)
            throw new UserNotFoundException("User with ID: '" + userId + "' not found");

        if (student.getUserRole() == UserRole.MENTOR)
            throw new DeleteAccountException("You can not delete account because this is mentor");

        final List<MeetingBooking> meetingsBookings = meetingBookingRepository.findAllByStudent(student);

        if (meetingsBookings != null) {

            for (MeetingBooking meetingBooking : meetingsBookings) {
                final Meeting meeting = meetingBooking.getMeeting();
                meeting.setBooked(false);
                meetingRepository.save(meeting);
            }
            meetingBookingRepository.deleteAll(meetingsBookings);
        }

        userRepository.delete(student);
    }

    public void deleteAccount(Principal principal) {

        final User student = userRepository.findByEmail(principal.getName());
        if (student.getUserRole() == UserRole.MENTOR)
            throw new DeleteAccountException("You can not delete account because you are mentor");

        final Optional<MeetingBooking> meetingBooking = meetingBookingRepository.findByStudent(student);

        if (meetingBooking.isPresent())
            throw new MeetingBookingAlreadyExistsException("You can not delete account because you have bookings");

        userRepository.delete(student);
    }

    public User signUpUser(User user) {

        final String encryptedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        user.setConfirmPassword("");

        final User savedUser;

        try {
            savedUser = userRepository.save(user);
        } catch (Exception e) {
            throw new UsernameAlreadyExistsException("Email '" + user.getEmail() + "' already exists");
        }

        final ConfirmationToken confirmationToken = new ConfirmationToken(user);
        final ConfirmationToken savedConfirmationToken = confirmationTokenService.saveConfirmationToken(confirmationToken);

        try {
            sendConfirmationMail(user.getEmail(), confirmationToken.getConfirmationToken());
        } catch (MessagingException ex) {
            userRepository.delete(user);
            confirmationTokenService.deleteConfirmationToken(savedConfirmationToken.getId());
            throw new UnableSendEmailException("Something went wrong. Please try again later");
        }

        savedUser.setPassword("");

        return savedUser;
    }

    public void confirmUser(ConfirmationToken confirmationToken) {

        final User user = confirmationToken.getUser();
        user.setEnabled(true);

        userRepository.save(user);

        confirmationTokenService.deleteConfirmationToken(confirmationToken.getId());
    }
}
