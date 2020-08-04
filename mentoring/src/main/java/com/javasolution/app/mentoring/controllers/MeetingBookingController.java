package com.javasolution.app.mentoring.controllers;

import com.javasolution.app.mentoring.entities.MeetingBooking;
import com.javasolution.app.mentoring.responses.CancelBookingResponse;
import com.javasolution.app.mentoring.services.MeetingBookingService;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class MeetingBookingController {

    private final MeetingBookingService meetingBookingService;

    @PostMapping("/meetings/{meetingId}/bookings")
    public ResponseEntity<?> bookingMeeting(@PathVariable String meetingId, Principal principal) {

        final MeetingBooking meetingBooking = meetingBookingService.bookingMeeting(meetingId, principal);

        return new ResponseEntity<>(meetingBooking, HttpStatus.OK);
    }

    @DeleteMapping("/bookings/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable String bookingId, Principal principal) {

        meetingBookingService.cancelBooking(bookingId, principal);

        return new ResponseEntity<>(new CancelBookingResponse("Meeting booking with ID: '" + bookingId + "' was deleted"),
                HttpStatus.OK);
    }

    @GetMapping("/bookings")
    public ResponseEntity<?> getAllBookings() {

        final Iterable<MeetingBooking> meetingsBookings = meetingBookingService.getAllBookings();

        return new ResponseEntity<>(meetingsBookings, HttpStatus.OK);
    }

    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity<?> getBooking(@PathVariable String bookingId) {

        final MeetingBooking meetingBooking = meetingBookingService.getBooking(bookingId);

        return new ResponseEntity<>(meetingBooking, HttpStatus.OK);
    }
}
