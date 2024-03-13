package com.example.back.model.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class History {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    private Date start_date;
    private Date end_date;
    private String rooms;
    private String description;
    private String user_name;

    public History(Date start_date, Date end_date, String rooms, String description, String user_name) {
        this.start_date = start_date;
        this.end_date = end_date;
        this.rooms = rooms;
        this.description = description;
        this.user_name = user_name;
    }
}
