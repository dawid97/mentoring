package com.javasolution.app.mentoring.repositories;

import com.javasolution.app.mentoring.entities.Meeting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository extends CrudRepository<Meeting, Long> {
    Iterable<Meeting> findAllByBooked(boolean isBooked);
}
