package com.example.back.model.requestsAndResponses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
@Getter
@Setter
public class RoomInfo {
    private Integer number;
    private Integer occupancy;
    private Integer numberOfReservations;
    private ArrayList<RoomReservation> roomReservation;
}
