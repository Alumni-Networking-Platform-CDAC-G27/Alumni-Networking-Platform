package com.anp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.anp.domain.entities.Picture;

import java.util.List;

@Repository
public interface PictureRepository extends JpaRepository<Picture, String> {
    List<Picture> findAllByUserId(String userId);
}
