package com.example.back.model.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "reservations")
@Getter
@Setter
@RequiredArgsConstructor
public class Reservations {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(name = "start_date")
    private Date start_date;
    @Column(name = "end_date")
    private Date end_date;
    @Column(name = "rooms")
    private String rooms;
    private String description;
    private String user_name;

    public Reservations(Date start_date, Date end_date, String rooms, String description, String user_name) {
        this.start_date = start_date;
        this.end_date = end_date;
        this.rooms = rooms;
        this.description = description;
        this.user_name = user_name;
    }
}
