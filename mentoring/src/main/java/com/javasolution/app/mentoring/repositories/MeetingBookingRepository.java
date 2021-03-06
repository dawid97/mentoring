package com.javasolution.app.mentoring.repositories;

import com.javasolution.app.mentoring.entities.MeetingBooking;
import com.javasolution.app.mentoring.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingBookingRepository extends CrudRepository<MeetingBooking, Long> {

    Optional<MeetingBooking> findByMeetingId(final Long id);

    List<MeetingBooking> findAllByStudent(final User student);

    Optional<MeetingBooking> findByStudent(final User student);
}
