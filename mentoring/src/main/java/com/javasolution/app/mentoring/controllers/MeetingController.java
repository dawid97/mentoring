package com.javasolution.app.mentoring.controllers;

import com.javasolution.app.mentoring.entities.Meeting;
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
}
