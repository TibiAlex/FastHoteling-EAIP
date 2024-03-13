package com.example.back.repository;

import com.example.back.model.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepo extends JpaRepository<Room, Long> {

    Optional<Room> findByNumber(Integer number);

}
