package com.example.back.service;

import com.example.back.handlers.NotLoggedInException;
import com.example.back.handlers.ReservationNotFoundException;
import com.example.back.handlers.UserNotFoundException;
import com.example.back.model.entities.AppUser;
import com.example.back.model.entities.Reservations;
import com.example.back.model.requestsAndResponses.AddReservationRequest;
import com.example.back.model.requestsAndResponses.DeleteReservationRequest;
import com.example.back.model.requestsAndResponses.ReservationInfo;
import com.example.back.model.requestsAndResponses.UpdateReservationRequest;
import com.example.back.repository.AppUserRepo;
import com.example.back.repository.ReservationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepo reservationRepo;
    private final AppUserRepo appUserRepo;

    private final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
    private final long MILLIS_IN_HALF_A_DAY = 1000 * 60 * 60 * 12;
    private final long MILLIS_IN_A_MINUTE = 1000 * 60;

    /**
     * metoda ce cauta toate rezervarile ce intersecteaza o anumita zi
     * @param day a Date variable
     * @return o lista de rezervari
     */
    public ResponseEntity<?> getCurrentReservations( Date day) {

        day.setTime((day.getTime() + MILLIS_IN_HALF_A_DAY + MILLIS_IN_A_MINUTE));

        List<Reservations> reservations = reservationRepo.findByStart_dateAfterAndEnd_dateBefore(day);
        List<ReservationInfo> reservationInfos = new ArrayList<>();

        for (Reservations reservation:reservations) {
            String[] str_rooms = reservation.getRooms().split(" ");
            ArrayList<Integer> rooms = new ArrayList<>();
            for (String str_room:str_rooms) {
                rooms.add(Integer.parseInt(str_room));
            }

            Date end_date = reservation.getEnd_date();
            end_date.setTime((end_date.getTime() - MILLIS_IN_A_DAY - MILLIS_IN_HALF_A_DAY));
            Date start_date = reservation.getStart_date();
            start_date.setTime((start_date.getTime() - MILLIS_IN_HALF_A_DAY - MILLIS_IN_A_MINUTE));

            reservationInfos.add(new ReservationInfo(reservation.getId(),
                    start_date,
                    end_date,
                    rooms,
                    reservation.getDescription(),
                    reservation.getUser_name()));
        }

        System.out.println("GetCurrentReservations date:" + day);
        return ResponseEntity.ok(reservationInfos);
    }

    /**
     * metoda ce adauga o rezervare in baza de date
     * @param req private Date start_date;
     *              private Date end_date;
     *              private String rooms;
     *              private String description;
     * @return mesaj de succes sau de eroare
     */
    public ResponseEntity<?> addReservation(AddReservationRequest req) {
        if(req.getStart_date().compareTo(req.getEnd_date()) > 0) {
            System.out.println("The start date is after the end date" + req.getStart_date() + " - " + req.getEnd_date());
            return ResponseEntity.ok("The start date is after the end date");
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof AppUser) {
            Long me_id = ((AppUser)principal).getId();
            Optional<AppUser> me_user = appUserRepo.findById(me_id);
            if (me_user.isPresent()) {
                req.getEnd_date().setTime(req.getEnd_date().getTime() + MILLIS_IN_HALF_A_DAY);
                req.getStart_date().setTime(req.getStart_date().getTime() + MILLIS_IN_HALF_A_DAY + MILLIS_IN_A_MINUTE);
                System.out.println("AddReservation: " + req.getStart_date() + " - " + req.getEnd_date());
                if(isValidReservation(req)) {
                    Reservations reservation = new Reservations(req.getStart_date(),
                            req.getEnd_date(),
                            req.getRooms(),
                            req.getDescription(),
                            me_user.get().getUsername());
                    reservationRepo.save(reservation);
                    System.out.println("AddReservation success: Rezervarea a fost adaugata" +
                            reservation.getStart_date() + " - " + reservation.getEnd_date());
                    return ResponseEntity.ok("Rezervarea a fost adaugata");
                } else {
                    System.out.println("AddReservation error: Rezervarea nu mai este disponibila");
                    return ResponseEntity.ok("Rezervarea nu mai este disponibila");
                }
            } else {
                throw new UserNotFoundException();
            }
        } else {
            throw new NotLoggedInException();
        }
    }

    /**
     * metoda de updatat o rezervare
     * @param req private Long id;
     *              private Date start_date;
     *              private Date end_date;
     *              private String rooms;
     *              private String description;
     * @return mesaj de succes sau de eroare
     */
    public ResponseEntity<?> updateReservation(UpdateReservationRequest req) {
        if(req.getStart_date().compareTo(req.getEnd_date()) > 0) {
            System.out.println("The start date is after the end date" + req.getStart_date() + " - " + req.getEnd_date());
            return ResponseEntity.ok("The start date is after the end date");
        }

        Optional<Reservations> reservations = reservationRepo.findById(req.getId());

        if(reservations.isPresent()) {

            if (req.getEnd_date() != null) {
                req.getEnd_date().setTime((req.getEnd_date().getTime() + MILLIS_IN_A_DAY + MILLIS_IN_HALF_A_DAY));
            }
            if (req.getStart_date() != null) {
                req.getStart_date().setTime(req.getStart_date().getTime() + MILLIS_IN_HALF_A_DAY + MILLIS_IN_A_MINUTE);
            }
            System.out.println("UpdateReservation: " + req.getStart_date() + " - " + req.getEnd_date());

            if(isValidUpdate(req)) {
                System.out.println("UpdateReservation error: Update-ul nu este posibil");
                return ResponseEntity.ok("Update-ul nu este posibil");
            }

            reservationRepo.update_start_Date(req.getStart_date(), req.getId());
            reservationRepo.update_end_Date(req.getEnd_date(), req.getId());
            if (!Objects.equals(req.getRooms(), "")) {
                reservationRepo.update_rooms(req.getRooms(), req.getId());
            }
            reservationRepo.update_description(req.getDescription(), req.getId());
            System.out.println("UpdateReservation success: Id:" + req.getId() + " Interval:" + req.getStart_date() + " - " + req.getEnd_date());
            return ResponseEntity.ok("Rezervarea a fost modificata");
        } else {
            throw new ReservationNotFoundException();
        }
    }

    /**
     * metoda ce sterge o rezervare
     * @param req private Long id;
     * @return mesaj de succes sau de eroare
     */
    public ResponseEntity<?> deleteReservation(DeleteReservationRequest req) {
        Optional<Reservations> reservations = reservationRepo.findById(req.getId());
        if(reservations.isPresent()) {
            reservationRepo.delete(reservations.get());
            System.out.println("DeleteReservation success: Reservation deleted with id " + reservations.get().getId());
            return ResponseEntity.ok("Rezervarea a fost stearsa");
        } else {
            throw new ReservationNotFoundException();
        }
    }

    /**
     * metoda ce verifica daca o rezervare este valida sau nu
     * @param req private Date start_date;
     *              private Date end_date;
     *              private String rooms;
     *              private String description;
     * @return true sau false
     */
    private Boolean isValidReservation(AddReservationRequest req) {

        if(req.getRooms().equals(""))
            return false;

        String[] occupiedRooms = reservationRepo.findReservationsBetweenDates(
                req.getStart_date(),
                req.getEnd_date());
        String[] toBeValidatedRooms = req.getRooms().split(" ");

        for (String occupiedRoom:occupiedRooms) {
            String[] rooms = occupiedRoom.split(" ");
            for (String room:rooms) {
                for (String toBeValidatedRoom:toBeValidatedRooms) {
                    if (Objects.equals(room, toBeValidatedRoom))
                        return false;
                }
            }
        }

        return true;
    }

    private Boolean isValidUpdate(UpdateReservationRequest req) {

        System.out.println("Update reservation isvalidUpdate: " + req.getRooms());

        if(req.getRooms().equals(""))
            return true;

        if(req.getStart_date() != null && req.getEnd_date() != null) {
            String[] occupiedRooms = reservationRepo.findReservationsBetweenDatesWithId(
                    req.getStart_date(),
                    req.getEnd_date(), req.getId());
            String[] toBeValidatedRooms = req.getRooms().split(" ");

            for (String occupiedRoom:occupiedRooms) {
                System.out.print(occupiedRoom + " ");
                String[] rooms = occupiedRoom.split(" ");
                for (String room:rooms) {
                    for (String toBeValidatedRoom:toBeValidatedRooms) {
                        if (Objects.equals(room, toBeValidatedRoom))
                            return true;
                    }
                }
            }
            System.out.println();
        }

        return false;
    }
}
