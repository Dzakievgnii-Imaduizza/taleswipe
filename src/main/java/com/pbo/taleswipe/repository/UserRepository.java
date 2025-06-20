package com.PBO.TaleSwipe.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.PBO.TaleSwipe.model.Story;
import com.PBO.TaleSwipe.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    @Query("SELECT s FROM Story s LEFT JOIN FETCH s.bookmarkedBy WHERE s.storyId = :storyId")
    Optional<Story> findByIdWithBookmarks(@Param("storyId") String storyId);
    

}
