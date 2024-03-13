package com.example.back.model.requestsAndResponses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
public class ReservationInfo {
    private Long id;
    private Date start_date;
    private Date end_date;
    private ArrayList<Integer> rooms;
    private String description;
    private String user_name;
}
