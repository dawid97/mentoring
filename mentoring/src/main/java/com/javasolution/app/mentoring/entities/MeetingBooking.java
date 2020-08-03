package com.javasolution.app.mentoring.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "MeetingsBookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createAt;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "student_id")
    private User student;

    @OneToOne(targetEntity = Meeting.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "meeting_id")
    private Meeting meeting;

    @PrePersist
    protected void onCreate() {
        this.createAt = LocalDateTime.now();
    }
}
