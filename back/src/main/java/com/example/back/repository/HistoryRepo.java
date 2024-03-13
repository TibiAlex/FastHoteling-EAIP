package com.example.back.repository;

import com.example.back.model.entities.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface HistoryRepo extends JpaRepository<History, Long> {
}
