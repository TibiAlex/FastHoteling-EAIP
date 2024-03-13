package com.example.back.controller;

import com.example.back.model.requestsAndResponses.AddRoomRequest;
import com.example.back.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/Benny")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdminController {

    private final AppUserService appUserService;


    /**
     * endpoint pentru a adauga camera noua
     * @param req  private Integer number;
     *             private Integer occupancy;
     * @return returns if the room was added or not
     */
    @PostMapping(path="/addRoom")
    public ResponseEntity<?> addRoom(@RequestBody AddRoomRequest req) {
        return appUserService.addRoom(req);
    }

    /**
     * endpoint pentru a verifica daca un user este admin
     * @return true if admin, false otherwise
     */
    @GetMapping(path="/isAdmin")
    public ResponseEntity<?> isAdmin() {
        return appUserService.isAdmin();
    }
}
