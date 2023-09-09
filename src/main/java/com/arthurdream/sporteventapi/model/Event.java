package com.arthurdream.sporteventapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class Event extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column
    private SportType sport;

    @Enumerated(EnumType.STRING)
    @Column
    private EventStatus status;

    @Column(nullable = false)
    private LocalDateTime startTime;
}
