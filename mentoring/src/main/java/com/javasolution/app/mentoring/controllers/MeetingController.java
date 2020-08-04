package com.javasolution.app.mentoring.controllers;

import com.javasolution.app.mentoring.entities.Meeting;
import com.javasolution.app.mentoring.responses.DeleteMeetingResponse;
import com.javasolution.app.mentoring.services.MapValidationErrorService;
import com.javasolution.app.mentoring.services.MeetingService;
import com.javasolution.app.mentoring.validators.MeetingValidator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/api/meetings")
public class MeetingController {

    private final MeetingService meetingService;
    private final MeetingValidator meetingValidator;
    private final MapValidationErrorService mapValidationErrorService;

    @PostMapping
    public ResponseEntity<?> addMeeting(@RequestBody Meeting meeting, BindingResult result) {

        meetingValidator.validate(meeting, result);

        final ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationService(result);
        if (errorMap != null) return errorMap;

        final List<Meeting> meetings = meetingService.addMeeting(meeting);

        return new ResponseEntity<>(meetings, HttpStatus.CREATED);
    }

    @DeleteMapping("/{meetingId}")
    public ResponseEntity<?> deleteMeeting(@PathVariable String meetingId) {

        meetingService.deleteMeeting(meetingId);

        return new ResponseEntity<>(new DeleteMeetingResponse("Meeting with ID: '" + meetingId + "' was deleted"),
                HttpStatus.OK);
    }

    @PutMapping("/{meetingId}")
    public ResponseEntity<?> updateMeeting(@PathVariable String meetingId, @RequestBody Meeting meeting, BindingResult result) {

        meetingValidator.validate(meeting, result);

        final ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationService(result);
        if (errorMap != null) return errorMap;

        final Meeting updatedMeeting = meetingService.updateMeeting(meetingId, meeting);

        return new ResponseEntity<>(updatedMeeting, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllMeetings() {

        final Iterable<Meeting> meetings = meetingService.getAllMeetings();

        return new ResponseEntity<>(meetings, HttpStatus.OK);
    }
}
