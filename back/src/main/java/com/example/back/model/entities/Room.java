package com.example.back.model.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Room {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    private Integer number;
    private Integer occupancy;

    public Room(Integer number, Integer occupancy) {
        this.number = number;
        this.occupancy = occupancy;
    }
}
