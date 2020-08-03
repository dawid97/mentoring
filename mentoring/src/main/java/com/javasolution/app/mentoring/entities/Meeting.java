package com.javasolution.app.mentoring.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity(name = "Meetings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate meetingDate;

    private LocalTime meetingStartTime;

    private LocalTime meetingEndTime;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    private Boolean booked;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "mentor_id")
    private User mentor;

    @PrePersist
    protected void onCreate() {
        this.createAt = LocalDateTime.now();
        this.booked = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateAt = LocalDateTime.now();
    }
}
