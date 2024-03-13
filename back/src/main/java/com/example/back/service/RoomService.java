package com.example.back.service;


import com.example.back.model.entities.Reservations;
import com.example.back.model.entities.Room;
import com.example.back.model.requestsAndResponses.RoomInfo;
import com.example.back.model.requestsAndResponses.RoomReservation;
import com.example.back.model.requestsAndResponses.SearchFreeRoomsRequest;
import com.example.back.repository.ReservationRepo;
import com.example.back.repository.RoomRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepo roomRepo;
    private final ReservationRepo reservationRepo;

    private final long MILLIS_IN_HALF_A_DAY = 1000 * 60 * 60 * 12;
    private final long MILLIS_IN_A_MINUTE = 1000 * 60;

    /**
     * UNUSED-------------------
     * fucntion that gets a list of all rooms and
     * @return it with information about all reservations
     */
    public ResponseEntity<?> getAllRooms() {

        List<Room> rooms = new ArrayList<>(roomRepo.findAll());
        HashMap<Integer, RoomInfo> roomInfos = new HashMap<>();
        List<Reservations> reservations = new ArrayList<>(reservationRepo.findAll());

        for (Room room : rooms) {
            RoomInfo roomInfo = new RoomInfo(room.getNumber(),
                    room.getOccupancy(),
                    0,
                    new ArrayList<>());
            roomInfos.put(roomInfo.getNumber(), roomInfo);
        }

        for (Reservations reservation:reservations) {
            RoomReservation roomReservation = new RoomReservation(reservation.getStart_date(),
                    reservation.getEnd_date(), reservation.getDescription(),
                    reservation.getUser_name());
            String[] arr_str_room_nr = reservation.getRooms().split(" ");
            for (String str_room_nr: arr_str_room_nr) {
                Integer room_nr = Integer.parseInt(str_room_nr);
                roomInfos.computeIfPresent(room_nr, (k, v) -> {
                    v.setNumberOfReservations(v.getNumberOfReservations() + 1);
                    v.getRoomReservation().add(roomReservation);
                    return v;
                });
            }
        }

        return ResponseEntity.ok(roomInfos);
    }

    /**
     * Function that returns free rooms on a specific day
     * @param day the current day
     * @return a list of free rooms
     */
    public ResponseEntity<?> getCurrentFreeRooms(Date day) {
        day.setTime((day.getTime() + MILLIS_IN_HALF_A_DAY + MILLIS_IN_A_MINUTE));
        String[] occupiedRooms = reservationRepo.findByStart_dateAfterOrEnd_dateBefore(day);

        List<Room> freeRooms = new ArrayList<>(roomRepo.findAll());

        System.out.println("GetCurrentFreeRooms success: " + day);
        return parseRooms(freeRooms, occupiedRooms);
    }

    public ResponseEntity<?> searchFreeRooms(SearchFreeRoomsRequest req) {

        if(req.getStart_date().compareTo(req.getEnd_date()) > 0) {
            System.out.println("The start date is after the end date" + req.getStart_date() + " - " + req.getEnd_date());
            return ResponseEntity.ok("The start date is after the end date");
        }

        if (req.getStart_date() == null || req.getEnd_date() == null) {
            System.out.println("SearchFreeRooms: One of the dates are null");
            return ResponseEntity.ok("One of the dates are null");
        }

        long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
        req.getEnd_date().setTime((req.getEnd_date().getTime() + MILLIS_IN_A_DAY + MILLIS_IN_HALF_A_DAY));
        req.getStart_date().setTime(req.getStart_date().getTime() + MILLIS_IN_HALF_A_DAY + MILLIS_IN_A_MINUTE);
        List<Room> freeRooms = new ArrayList<>(roomRepo.findAll());

        String[] occupiedRooms = reservationRepo.findReservationsBetweenDates(
                        req.getStart_date(),
                        req.getEnd_date());

        System.out.println("SearchFreeRooms: " + req.getStart_date() + " - " + req.getEnd_date());
        return parseRooms(freeRooms, occupiedRooms);
    }

    /**
     * method that gets a list of all rooms and remove the occupied ones
     * @param freeRooms list of all rooms
     * @param occupiedRooms list of occupied rooms
     * @return list of free rooms in a period of time
     */
    private ResponseEntity<?> parseRooms(List<Room> freeRooms, String[] occupiedRooms) {
        for (String occupiedRoom:occupiedRooms) {
            String[] rooms = occupiedRoom.split(" ");
            for (String room:rooms) {
                int room_nr = Integer.parseInt(room);
                freeRooms.removeIf(r -> r.getNumber() == room_nr);
            }
        }

        return ResponseEntity.ok(freeRooms);
    }
}
