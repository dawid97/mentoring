package com.javasolution.app.mentoring.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.javasolution.app.mentoring.entities.Meeting;
import com.javasolution.app.mentoring.entities.User;
import com.javasolution.app.mentoring.entities.UserRole;
import com.javasolution.app.mentoring.exceptions.InvalidCastException;
import com.javasolution.app.mentoring.exceptions.MeetingNotFoundException;
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
class MeetingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    ObjectMapper mapper = new ObjectMapper();

    private User mentor;
    private User student;
    private Meeting meeting;

    Long mentorId;
    Long studentId;
    Long meetingId;

    @BeforeEach
    void setUp() {
        createMentor();
        createStudent();
        createMeeting();
    }

    @AfterEach
    void tearDown() {
        meetingRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getAllMeetings() throws Exception {

        assertEquals(1, meetingRepository.count());
        final String jwt = login(student.getEmail(), student.getPassword());

        final MvcResult result = mockMvc.perform(get("/api/meetings")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andReturn();

        final List<Meeting> meetings = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals(1, meetings.size());
        assertEquals(meetingId, meetings.get(0).getId());
    }

    @Test
    void getMeeting_meetingInDatabase_meetingReturnedSuccessfully() throws Exception {

        assertEquals(1, meetingRepository.count());
        final String jwt = login(mentor.getEmail(), mentor.getPassword());

        final MvcResult result = mockMvc.perform(get("/api/meetings/{meetingId}", meetingId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andReturn();

        final Meeting meeting = parseResponse(result, Meeting.class);
        assertNotNull(meeting);
        assertEquals(meetingId, meeting.getId());
    }

    @Test
    void getMeeting_meetingNotInDatabase_meetingNotFoundException() throws Exception {

        assertEquals(1, meetingRepository.count());
        final String jwt = login(mentor.getEmail(), mentor.getPassword());
        final String wrongMeetingId = "12345";

        mockMvc.perform(get("/api/meetings/{meetingId}", wrongMeetingId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MeetingNotFoundException))
                .andExpect(result -> assertEquals("Meeting with ID: '" + wrongMeetingId + "' was not found"
                        , Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getMeeting_meetingNotInDatabase_invalidCastException() throws Exception {

        assertEquals(1, meetingRepository.count());
        final String jwt = login(mentor.getEmail(), mentor.getPassword());
        final String wrongMeetingId = "as";

        mockMvc.perform(get("/api/meetings/{meetingId}", wrongMeetingId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidCastException))
                .andExpect(result -> assertEquals("Meeting id have to be long type"
                        , Objects.requireNonNull(result.getResolvedException()).getMessage()));
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