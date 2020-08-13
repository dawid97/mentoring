package com.javasolution.app.mentoring.controllers;

import com.javasolution.app.mentoring.entities.MeetingBooking;
import com.javasolution.app.mentoring.responses.CancelBookingResponse;
import com.javasolution.app.mentoring.services.MeetingBookingService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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

    @ApiImplicitParams({@ApiImplicitParam(name = "authorization", value = "Bearer + Json Web Token", paramType = "header")})
    @ApiOperation(value = "The Booking Meeting Web Service Endpoint")
    @PostMapping("/meetings/{meetingId}/bookings")
    public ResponseEntity<?> bookingMeeting(@PathVariable final String meetingId, final Principal principal) {

        final MeetingBooking meetingBooking = meetingBookingService.bookingMeeting(meetingId, principal);

        return new ResponseEntity<>(meetingBooking, HttpStatus.OK);
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "authorization", value = "Bearer + Json Web Token", paramType = "header")})
    @ApiOperation(value = "The Delete Booking Web Service Endpoint")
    @DeleteMapping("/bookings/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable final String bookingId, final Principal principal) {

        meetingBookingService.cancelBooking(bookingId, principal);

        return new ResponseEntity<>(new CancelBookingResponse("Meeting booking with ID: '" + bookingId + "' was deleted"),
                HttpStatus.OK);
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "authorization", value = "Bearer + Json Web Token", paramType = "header")})
    @ApiOperation(value = "The Get All Bookings Web Service Endpoint")
    @GetMapping("/bookings")
    public ResponseEntity<?> getAllBookings() {

        final Iterable<MeetingBooking> meetingsBookings = meetingBookingService.getAllBookings();

        return new ResponseEntity<>(meetingsBookings, HttpStatus.OK);
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "authorization", value = "Bearer + Json Web Token", paramType = "header")})
    @ApiOperation(value = "The Get Booking Web Service Endpoint")
    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity<?> getBooking(@PathVariable final String bookingId) {

        final MeetingBooking meetingBooking = meetingBookingService.getBooking(bookingId);

        return new ResponseEntity<>(meetingBooking, HttpStatus.OK);
    }


    @ApiImplicitParams({@ApiImplicitParam(name = "authorization", value = "Bearer + Json Web Token", paramType = "header")})
    @ApiOperation(value = "The Get My Booking Web Service Endpoint")
    @GetMapping("/bookings/me/{bookingId}")
    public ResponseEntity<?> getMyBooking(@PathVariable final String bookingId, final Principal principal) {

        final MeetingBooking meetingBooking = meetingBookingService.getMyBooking(bookingId, principal);

        return new ResponseEntity<>(meetingBooking, HttpStatus.OK);
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "authorization", value = "Bearer + Json Web Token", paramType = "header")})
    @ApiOperation(value = "The Get All My Bookings Web Service Endpoint")
    @GetMapping("/bookings/me")
    public ResponseEntity<?> getAllMyBookings(final Principal principal) {

        final Iterable<MeetingBooking> meetingsBookings = meetingBookingService.getAllMyBookings(principal);

        return new ResponseEntity<>(meetingsBookings, HttpStatus.OK);
    }
}
