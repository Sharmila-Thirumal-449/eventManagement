package com.eventManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    private String location;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private int capacity;

    private boolean published = true;

    private String category;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "event_speakers",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "speaker_id")
    )
    private Set<Speaker> speakers = new HashSet<>();
}
