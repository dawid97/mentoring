package com.javasolution.app.mentoring.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.javasolution.app.mentoring.entities.Meeting;
import com.javasolution.app.mentoring.entities.MeetingBooking;
import com.javasolution.app.mentoring.entities.User;
import com.javasolution.app.mentoring.entities.UserRole;
import com.javasolution.app.mentoring.exceptions.InvalidCastException;
import com.javasolution.app.mentoring.repositories.MeetingBookingRepository;
import com.javasolution.app.mentoring.repositories.MeetingRepository;
import com.javasolution.app.mentoring.repositories.UserRepository;
import com.javasolution.app.mentoring.requests.LoginRequest;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class MeetingBookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private MeetingBookingRepository meetingBookingRepository;

    @Autowired
    ObjectMapper mapper = new ObjectMapper();

    private User mentor;
    private User student;
    private Meeting meeting;
    private MeetingBooking meetingBooking;

    Long mentorId;
    Long studentId;
    Long meetingId;
    Long bookingId;

    @BeforeEach
    void setUp() {
        createMentor();
        createStudent();
        createMeeting();
        createBooking();
    }

    @AfterEach
    void tearDown() {
        meetingBookingRepository.deleteAll();
        meetingRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getBooking_wrongMeetingBookingIdType_invalidCastException() throws Exception {

        assertEquals(1, meetingBookingRepository.count());
        final String wrongBookingId = "as";
        final String jwt = login(mentor.getEmail(), mentor.getPassword());

        mockMvc.perform(get("/api/bookings/{bookingId}", wrongBookingId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidCastException))
                .andExpect(result -> assertEquals("Meeting booking id have to be long type"
                        , Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getBooking_bookingReturnedSuccessfully() throws Exception {

        assertEquals(1, meetingBookingRepository.count());
        final String jwt = login(mentor.getEmail(), mentor.getPassword());

        final MvcResult result = mockMvc.perform(get("/api/bookings/{bookingId}", bookingId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andReturn();

        final MeetingBooking meetingBooking = parseResponse(result, MeetingBooking.class);
        assertNotNull(meetingBooking);
        assertEquals(bookingId, meetingBooking.getId());
    }

    @Test
    void getAllMyBookings() throws Exception {

        assertEquals(1, meetingBookingRepository.count());
        final String jwt = login(student.getEmail(), student.getPassword());

        final MvcResult result = mockMvc.perform(get("/api/bookings/me/")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andReturn();

        final List<MeetingBooking> bookings = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals(1, bookings.size());
        assertEquals(bookingId, bookings.get(0).getId());
        assertEquals(studentId, bookings.get(0).getStudent().getId());
        assertEquals(meetingId, bookings.get(0).getMeeting().getId());
    }

    private String login(String username, String password) throws Exception {

        final LoginRequest loginRequest = new LoginRequest(username, password);

        final Gson gson = new Gson();
        final String requestJson = gson.toJson(loginRequest);

        final MvcResult result = mockMvc.perform(post("/api/users/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        final String response = result.getResponse().getContentAsString();
        final JSONObject responseJson = new JSONObject(response);

        return responseJson.getString("jwt");
    }

    private void createStudent() {

        //create student
        student = new User();
        student.setPassword("pass123456");
        student.setPassword(bCryptPasswordEncoder.encode(student.getPassword()));
        student.setConfirmPassword("");
        student.setName("Dawid");
        student.setSurname("Ulfik");
        student.setEmail("dawulf97@gmail.com");
        student.setUserRole(UserRole.STUDENT);
        student.setEnabled(true);
        final User savedStudent = userRepository.save(student);
        studentId = savedStudent.getId();
        student.setPassword("pass123456");
    }

    private void createBooking() {

        meetingBooking = new MeetingBooking();
        final Optional<User> student = userRepository.findById(studentId);
        student.ifPresent(user -> meetingBooking.setStudent(user));
        meetingBooking.setCreateAt(LocalDateTime.now());
        final Optional<Meeting> meeting = meetingRepository.findById(meetingId);
        meeting.ifPresent(value -> meetingBooking.setMeeting(value));
        final MeetingBooking savedMeetingBooking = meetingBookingRepository.save(meetingBooking);
        bookingId = savedMeetingBooking.getId();
    }

    private void createMentor() {

        //create mentor
        mentor = new User();
        mentor.setPassword("pass999967");
        mentor.setPassword(bCryptPasswordEncoder.encode(mentor.getPassword()));
        mentor.setConfirmPassword("");
        mentor.setName("Daniel");
        mentor.setSurname("Kowalski");
        mentor.setEmail("dan.kow94@gmail.com");
        mentor.setUserRole(UserRole.MENTOR);
        mentor.setEnabled(true);
        final User savedMentor = userRepository.save(mentor);
        mentorId = savedMentor.getId();
        mentor.setPassword("pass999967");
    }

    private void createMeeting() {

        //create meeting
        meeting = new Meeting();
        meeting.setMeetingDate(LocalDate.now());
        meeting.setMeetingStartTime(LocalTime.of(19, 0, 0, 0));
        meeting.setMeetingEndTime(LocalTime.of(19, 15, 0, 0));
        meeting.setBooked(false);
        meeting.setCreateAt(LocalDateTime.now());
        final Optional<User> mentor = userRepository.findById(mentorId);
        mentor.ifPresent(meeting::setMentor);
        final Meeting savedMeeting = meetingRepository.save(meeting);
        meetingId = savedMeeting.getId();
    }

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    public static <T> T parseResponse(MvcResult result, Class<T> responseClass) {
        try {
            String contentAsString = result.getResponse().getContentAsString();
            return MAPPER.readValue(contentAsString, responseClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}