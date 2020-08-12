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
import com.javasolution.app.mentoring.exceptions.*;
import com.javasolution.app.mentoring.repositories.ConfirmationTokenRepository;
import com.javasolution.app.mentoring.repositories.MeetingBookingRepository;
import com.javasolution.app.mentoring.repositories.MeetingRepository;
import com.javasolution.app.mentoring.repositories.UserRepository;
import com.javasolution.app.mentoring.requests.LoginRequest;
import com.javasolution.app.mentoring.requests.UpdateUserRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MeetingBookingRepository meetingBookingRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private UserRepository userRepository;

    private User mentor;
    private User student;

    Long mentorId;
    Long studentId;
    Long unconfirmedStudentId;

    @BeforeEach
    void setUp() {
        addMentor();
        addStudent();
    }

    @AfterEach
    void tearDown() {
        meetingBookingRepository.deleteAll();
        meetingRepository.deleteAll();
        confirmationTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    private void addStudent() {

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

    private void addMentor() {

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

    @Test
    void registerUser_userRegistered() throws Exception {

        assertEquals(2, userRepository.count());
        final User registerUserRequest = new User();
        registerUserRequest.setEmail("dawid_19_97@interia.pl");
        registerUserRequest.setName("Tomek");
        registerUserRequest.setSurname("Malolepszy");
        registerUserRequest.setPassword("pass123456");
        registerUserRequest.setConfirmPassword("pass123456");
        final Gson gson = new Gson();
        final String requestJson = gson.toJson(registerUserRequest);

        MvcResult result = mockMvc.perform(post("/api/users/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andReturn();

        final User user = parseResponse(result, User.class);
        assertNotNull(user);
        assertEquals(registerUserRequest.getEmail(), user.getEmail());
        assertEquals(registerUserRequest.getName(), user.getName());
        assertEquals(registerUserRequest.getSurname(), user.getSurname());
        assertEquals(3, userRepository.count());
        unconfirmedStudentId = user.getId();
    }

    @Test
    void registerUser_usernameAlreadyExistsException() throws Exception {

        assertEquals(2, userRepository.count());
        final User registerUserRequest = new User();
        registerUserRequest.setEmail("dawulf97@gmail.com");
        registerUserRequest.setName("Dawid");
        registerUserRequest.setSurname("Bula");
        registerUserRequest.setPassword("pass123456");
        registerUserRequest.setConfirmPassword("pass123456");
        final Gson gson = new Gson();
        final String requestJson = gson.toJson(registerUserRequest);

        mockMvc.perform(post("/api/users/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UsernameAlreadyExistsException))
                .andExpect(result -> assertEquals("Email '" + registerUserRequest.getEmail() + "' already exists"
                        , Objects.requireNonNull(result.getResolvedException()).getMessage()));

        assertEquals(2, userRepository.count());
    }

    @Test
    void deleteUser_userInDatabase_userDeleted() throws Exception {

        //create meeting
        final Meeting meeting = new Meeting();
        meeting.setMeetingDate(LocalDate.now());
        meeting.setMeetingStartTime(LocalTime.of(19, 0, 0, 0));
        meeting.setMeetingEndTime(LocalTime.of(19, 15, 0, 0));
        meeting.setCreateAt(LocalDateTime.now());
        final Optional<User> foundMentor = userRepository.findById(mentorId);
        foundMentor.ifPresent(meeting::setMentor);
        Meeting unbookedMeeting = meetingRepository.save(meeting);
        unbookedMeeting.setBooked(true);
        final Meeting savedMeeting = meetingRepository.save(unbookedMeeting);

        //create booking
        final MeetingBooking meetingBooking = new MeetingBooking();
        meetingBooking.setMeeting(savedMeeting);
        meetingBooking.setCreateAt(LocalDateTime.now());
        final Optional<User> foundStudent = userRepository.findById(studentId);
        foundStudent.ifPresent(meetingBooking::setStudent);
        meetingBookingRepository.save(meetingBooking);


        assertEquals(true, savedMeeting.getBooked());
        assertEquals(1, meetingBookingRepository.count());
        assertEquals(1, meetingRepository.count());
        assertEquals(2, userRepository.count());
        final String jwt = login(mentor.getEmail(), mentor.getPassword());

        MvcResult result = mockMvc.perform(delete("/api/users/{userId}", studentId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andReturn();

        final String response = result.getResponse().getContentAsString();
        final JSONObject responseJson = new JSONObject(response);
        assertEquals("Account with ID: '" + studentId + "' deleted successfully", responseJson.getString("user"));
        assertEquals(1, userRepository.count());
        assertEquals(0, meetingBookingRepository.count());

        Meeting foundMeeting = meetingRepository.findById(savedMeeting.getId()).get();
        assertEquals(false, foundMeeting.getBooked());
    }

    @Test
    void deleteUser_userNotInDatabase_userNotFoundException() throws Exception {

        assertEquals(2, userRepository.count());
        final String userId = "123456";
        final String jwt = login(mentor.getEmail(), mentor.getPassword());

        mockMvc.perform(delete("/api/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals("User with ID: '" + userId + "' not found"
                        , Objects.requireNonNull(result.getResolvedException()).getMessage()));

        assertEquals(2, userRepository.count());
    }

    @Test
    void deleteUser_userInDatabase_deleteAccountException() throws Exception {

        assertEquals(2, userRepository.count());
        final String jwt = login(mentor.getEmail(), mentor.getPassword());

        mockMvc.perform(delete("/api/users/{userId}", mentorId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DeleteAccountException))
                .andExpect(result -> assertEquals("You can not delete account because this is mentor"
                        , Objects.requireNonNull(result.getResolvedException()).getMessage()));

        assertEquals(2, userRepository.count());
    }

    @Test
    void deleteAccount_userInDatabase_userDeleted() throws Exception {

        assertEquals(2, userRepository.count());
        final String jwt = login(student.getEmail(), student.getPassword());

        final MvcResult result = mockMvc.perform(delete("/api/users/me/")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andReturn();

        final String response = result.getResponse().getContentAsString();
        final JSONObject responseJson = new JSONObject(response);
        assertEquals("Account deleted successfully", responseJson.getString("user"));
        assertEquals(1, userRepository.count());
    }

    @Test
    void deleteAccount_userInDatabase_MeetingBookingAlreadyExistsException() throws Exception {

        //create meeting
        final Meeting meeting = new Meeting();
        meeting.setMeetingDate(LocalDate.now());
        meeting.setMeetingStartTime(LocalTime.of(19, 0, 0, 0));
        meeting.setMeetingEndTime(LocalTime.of(19, 15, 0, 0));
        meeting.setBooked(false);
        meeting.setCreateAt(LocalDateTime.now());
        final Optional<User> mentor = userRepository.findById(mentorId);
        mentor.ifPresent(meeting::setMentor);
        final Meeting savedMeeting = meetingRepository.save(meeting);

        //create booking
        final MeetingBooking meetingBooking = new MeetingBooking();
        meetingBooking.setMeeting(savedMeeting);
        meetingBooking.setCreateAt(LocalDateTime.now());
        final Optional<User> student = userRepository.findById(studentId);
        student.ifPresent(meetingBooking::setStudent);
        meetingBookingRepository.save(meetingBooking);

        assertEquals(2, userRepository.count());
        assertEquals(1, meetingBookingRepository.count());
        assertEquals(1, meetingRepository.count());

        final String jwt = login(this.student.getEmail(), this.student.getPassword());

        mockMvc.perform(delete("/api/users/me/")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MeetingBookingAlreadyExistsException))
                .andExpect(result -> assertEquals("You can not delete account because you have bookings"
                        , Objects.requireNonNull(result.getResolvedException()).getMessage()));

        assertEquals(2, userRepository.count());
    }

    @Test
    void updateMe() throws Exception {

        final String updatedName = "Dawidek";
        final String updatedSurname = "Ulfikos";
        final UpdateUserRequest updateUserRequest = new UpdateUserRequest(updatedName, updatedSurname);
        final String jwt = login(student.getEmail(), student.getPassword());
        final Gson gson = new Gson();
        final String requestJson = gson.toJson(updateUserRequest);

        final MvcResult result = mockMvc.perform(put("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andReturn();

        final User user = parseResponse(result, User.class);
        assertEquals(updatedName, user.getName());
        assertEquals(updatedSurname, user.getSurname());
    }

    @Test
    void getAllUsers() throws Exception {

        final String jwt = login(mentor.getEmail(), mentor.getPassword());

        final MvcResult result = mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andReturn();

        final List<User> users = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals(2, users.size());
        assertEquals(mentorId, users.get(0).getId());
        assertEquals(mentor.getEmail(), users.get(0).getEmail());
        assertEquals(studentId, users.get(1).getId());
        assertEquals(student.getEmail(), users.get(1).getEmail());
    }

    @Test
    void getMe() throws Exception {

        final String jwt = login(student.getEmail(), student.getPassword());

        mockMvc.perform(get("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(student.getEmail()));
    }

    @Test
    void authenticateUser_userInDatabase_userReceivedAccess() throws Exception {

        final LoginRequest loginRequest = new LoginRequest(mentor.getEmail(), mentor.getPassword());
        final Gson gson = new Gson();
        final String requestJson = gson.toJson(loginRequest);

        final MvcResult result = mockMvc.perform(post("/api/users/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        final String responseJson = result.getResponse().getContentAsString();
        assertTrue(responseJson.contains("jwt"));
    }

    @Test
    void authenticateUser_userNotInDatabase_userNotReceivedAccess() throws Exception {

        final LoginRequest loginRequest = new LoginRequest("thomas5678@gmail.com", "p123456789");
        final Gson gson = new Gson();
        final String requestJson = gson.toJson(loginRequest);

        mockMvc.perform(post("/api/users/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUser_userInDatabase_userFound() throws Exception {

        final String jwt = login(mentor.getEmail(), mentor.getPassword());

        final MvcResult result = mockMvc.perform(get("/api/users/{userId}", studentId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andReturn();

        final User user = parseResponse(result, User.class);
        assertNotNull(user);
        assertEquals(studentId, user.getId());
    }

    @Test
    void getUser_userNotInDatabase_userNotFoundException() throws Exception {

        final String userId = "123";
        final String jwt = login(mentor.getEmail(), mentor.getPassword());

        mockMvc.perform(get("/api/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals("User with ID: '" + userId + "' was not found"
                        , Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getUser_wrongIdFormat_invalidCastException() throws Exception {

        final String userId = "as";
        final String jwt = login(mentor.getEmail(), mentor.getPassword());

        mockMvc.perform(get("/api/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidCastException))
                .andExpect(result -> assertEquals("User id have to be long type"
                        , Objects.requireNonNull(result.getResolvedException()).getMessage()));
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
}