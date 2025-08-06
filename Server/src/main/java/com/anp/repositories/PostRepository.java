package com.anp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.anp.domain.entities.Post;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {

    List<Post> findAllByTimelineUserIdOrderByTimeDesc(String timelineUserId);
}
