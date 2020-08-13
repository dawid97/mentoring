package com.javasolution.app.mentoring.controllers;

import com.javasolution.app.mentoring.entities.Meeting;
import com.javasolution.app.mentoring.responses.DeleteMeetingResponse;
import com.javasolution.app.mentoring.services.MapValidationErrorService;
import com.javasolution.app.mentoring.services.MeetingService;
import com.javasolution.app.mentoring.validators.MeetingValidator;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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

    @ApiImplicitParams({@ApiImplicitParam(name = "authorization", value = "Bearer + Json Web Token", paramType = "header")})
    @ApiOperation(value = "The Add Meeting Web Service Endpoint")
    @PostMapping
    public ResponseEntity<?> addMeeting(@RequestBody final Meeting meeting, final BindingResult result) {

        meetingValidator.validate(meeting, result);

        final ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationService(result);
        if (errorMap != null) return errorMap;

        final List<Meeting> meetings = meetingService.addMeeting(meeting);

        return new ResponseEntity<>(meetings, HttpStatus.CREATED);
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "authorization", value = "Bearer + Json Web Token", paramType = "header")})
    @ApiOperation(value = "The Delete Meeting Web Service Endpoint")
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<?> deleteMeeting(@PathVariable final String meetingId) {

        meetingService.deleteMeeting(meetingId);

        return new ResponseEntity<>(new DeleteMeetingResponse("Meeting with ID: '" + meetingId + "' was deleted"),
                HttpStatus.OK);
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "authorization", value = "Bearer + Json Web Token", paramType = "header")})
    @ApiOperation(value = "The Update Meeting Web Service Endpoint")
    @PutMapping("/{meetingId}")
    public ResponseEntity<?> updateMeeting(@PathVariable final String meetingId, @RequestBody final Meeting meeting, final BindingResult result) {

        meetingValidator.validate(meeting, result);

        final ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationService(result);
        if (errorMap != null) return errorMap;

        final Meeting updatedMeeting = meetingService.updateMeeting(meetingId, meeting);

        return new ResponseEntity<>(updatedMeeting, HttpStatus.OK);
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "authorization", value = "Bearer + Json Web Token", paramType = "header")})
    @ApiOperation(value = "The Get All Meetings Web Service Endpoint")
    @GetMapping
    public ResponseEntity<?> getAllMeetings() {

        final Iterable<Meeting> meetings = meetingService.getAllMeetings();

        return new ResponseEntity<>(meetings, HttpStatus.OK);
    }


    @ApiImplicitParams({@ApiImplicitParam(name = "authorization", value = "Bearer + Json Web Token", paramType = "header")})
    @ApiOperation(value = "The Get Meeting Web Service Endpoint")
    @GetMapping("/{meetingId}")
    public ResponseEntity<?> getMeeting(@PathVariable final String meetingId) {

        final Meeting meeting = meetingService.getMeeting(meetingId);

        return new ResponseEntity<>(meeting, HttpStatus.OK);
    }
}
