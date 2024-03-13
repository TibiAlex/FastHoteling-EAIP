package com.example.back.controller;

import com.example.back.model.requestsAndResponses.SearchFreeRoomsRequest;
import com.example.back.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping(path = "/Benny")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RoomController {

    private final RoomService roomService;

    /**
     * endpoint pentru a lua toate camere cu info despre ele, inclusiv rezervarile pe camere
     * @return lista cu toate camerele si info despre ele
     */
    @GetMapping(path= "/getAllRooms")
    public ResponseEntity<?> getAllRooms() {
        return roomService.getAllRooms();
    }

    /**
     * endpoint pentru a lua toate camerele libere azi
     * @param day a Date value
     * @return lista cu toate camerele libere in ziua respectiva
     */
    @GetMapping(path= "/getCurrentFreeRooms")
    public ResponseEntity<?> getCurrentFreeRooms(@RequestParam @DateTimeFormat(pattern="yyyy/MM/dd") Date day) {
        return roomService.getCurrentFreeRooms(day);
    }

    /**
     * endpoint pentru cautarea camerelor libere intr-o anumita perioada
     * @param req  private Date start_date;
     *              private Date end_date;
     * @return lista cu camerele libere in perioada respectiva
     */
    @PostMapping(path= "/searchFreeRooms")
    public ResponseEntity<?> searchFreeRooms(@RequestBody SearchFreeRoomsRequest req) {
        return roomService.searchFreeRooms(req);
    }
}
