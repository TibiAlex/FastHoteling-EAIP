package com.example.back.controller;

import com.example.back.model.requestsAndResponses.AddReservationRequest;
import com.example.back.model.requestsAndResponses.DeleteReservationRequest;
import com.example.back.model.requestsAndResponses.UpdateReservationRequest;
import com.example.back.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping(path = "/Benny")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ReservationController {

    private final ReservationService reservationService;


    /**
     * endpoint cu toate rezervarile de azi
     * @param day a Date value
     * @return a list with all reservations intersectidng that date
     */
    @GetMapping(path = "/getCurrentReservations")
    public ResponseEntity<?> getCurrentReservations(@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date day) {
        return reservationService.getCurrentReservations(day);
    }

    /**
     * rooms sunt trimise ca string
     * majoritatea campurilor pot fi goale, caz in care sunt trimise "" sau false sau null
     * endpoint pentru adaugarea unei rezervari
     * @param req private Date start_date;
     *              private Date end_date;
     *              private String rooms;
     *              private String description;
     * @return mesaj de succes daca merge sau error daca nu
     */
    @PostMapping(path = "/addReservation")
    public ResponseEntity<?> addReservation(@RequestBody AddReservationRequest req) {
        System.out.println(req);
        return reservationService.addReservation(req);
    }

    /**
     * endpoint ce se ocupa cu update-ul unei rezervari existente, campurile care nu trebuie updatate
     * sunt trimise ca null
     * @param req private Long id;
     *              private Date start_date;
     *              private Date end_date;
     *              private String rooms;
     *              private String description;
     * @return mesaj de succes daca merge sau error daca nu
     */
    @PostMapping(path = "/updateReservation")
    public ResponseEntity<?> updateReservation(@RequestBody UpdateReservationRequest req) {
        return reservationService.updateReservation(req);
    }

    /**
     * endpoint to delete an existing reservation
     * @param req private Long id;
     * @return mesaj de succes daca merge sau error daca nu
     */
    @PostMapping(path = "/deleteReservation")
    public ResponseEntity<?> deleteReservation(@RequestBody DeleteReservationRequest req) {
        return reservationService.deleteReservation(req);
    }
}
