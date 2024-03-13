package com.example.back.model.requestsAndResponses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
public class SearchFreeRoomsRequest {
    private Date start_date;
    private Date end_date;
}
