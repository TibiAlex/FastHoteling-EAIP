package com.example.back.model.requestsAndResponses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
public class RoomReservation {
    private Date start_date;
    private Date end_date;
    private String description;
    private String user_name;
}
