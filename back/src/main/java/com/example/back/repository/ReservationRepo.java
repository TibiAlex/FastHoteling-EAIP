package com.example.back.repository;

import com.example.back.model.entities.Reservations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
@Repository
public interface ReservationRepo extends JpaRepository<Reservations, Long> {

    @Query("select rooms from reservations where ?1 between start_date and end_date")
    String[] findByStart_dateAfterOrEnd_dateBefore(Date day);

    @Query("select r from reservations r where ?1 between r.start_date and r.end_date")
    List<Reservations> findByStart_dateAfterAndEnd_dateBefore(Date day);

    @Modifying
    @Query(value = "update reservations set start_date = ifnull(?1, start_date) where id = ?2"
            ,nativeQuery = true)
    void update_start_Date(Date start_date, Long id);

    @Modifying
    @Query(value = "update reservations set end_date = ifnull(?1, end_date) where id = ?2"
            ,nativeQuery = true)
    void update_end_Date(Date end_date, Long id);

    @Modifying
    @Query(value = "update reservations set rooms = ifnull(?1, rooms) where id = ?2"
            ,nativeQuery = true)
    void update_rooms(String rooms, Long id);

    @Modifying
    @Query(value = "update reservations set description = ifnull(?1, description) where id = ?2"
            ,nativeQuery = true)
    void update_description(String description, Long id);

    @Query("select r.rooms from reservations r " +
            "where r.start_date between ?1 and ?2 " +
            "or r.end_date between ?1 and ?2 " +
            "or r.start_date < ?1 and r.end_date > ?2")
    String[] findReservationsBetweenDates(Date start_date, Date end_date);

    @Query("select r.rooms from reservations r " +
            "where r.id <> ?3 " +
            "and (r.start_date between ?1 and ?2 " +
            "or r.end_date between ?1 and ?2 " +
            "or r.start_date < ?1 and r.end_date > ?2) ")
    String[] findReservationsBetweenDatesWithId(Date start_date, Date end_date, Long id);

    @Query("select r from reservations r where r.end_date < ?1")
    List<Reservations> getOldReservations(Date current_date);
}
