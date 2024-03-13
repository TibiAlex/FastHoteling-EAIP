package com.example.back.model.requestsAndResponses;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AddRoomRequest {
    private Integer number;
    private Integer occupancy;
}
