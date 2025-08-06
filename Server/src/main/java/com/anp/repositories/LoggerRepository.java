package com.anp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.anp.domain.entities.Logger;

import jakarta.transaction.Transactional;

@Repository
public interface LoggerRepository extends JpaRepository<Logger, String> {
    List<Logger> findAllByOrderByTimeDesc();

    List<Logger> findAllByUsernameOrderByTimeDesc(String username);

    @Transactional
    @Modifying
    List<Logger> deleteAllByUsername(String username);
}