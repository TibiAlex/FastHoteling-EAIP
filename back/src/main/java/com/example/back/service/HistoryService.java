package com.example.back.service;

import com.example.back.model.entities.History;
import com.example.back.model.entities.Reservations;
import com.example.back.repository.HistoryRepo;
import com.example.back.repository.ReservationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepo historyRepo;
    private final ReservationRepo reservationRepo;

    /**
     * functie ce muta toate rezervarile vechi in tabela de history
     * @return mesaj de success sau eroare in consola
     */
    public String moveOldReservations() {
        long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
        Date date = new Date(System.currentTimeMillis());
        date.setTime(date.getTime() - MILLIS_IN_A_DAY * 30);
        List<Reservations> reservations = reservationRepo.getOldReservations(date);
        for (Reservations reservation:reservations) {
            historyRepo.save(new History(reservation.getStart_date(),
                    reservation.getEnd_date(),
                    reservation.getRooms(),
                    reservation.getDescription(),
                    reservation.getUser_name()));
            reservationRepo.delete(reservation);
        }
        return "The old reservations have been moved";
    }
}
