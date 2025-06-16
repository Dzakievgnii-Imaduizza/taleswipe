package com.PBO.TaleSwipe.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.PBO.TaleSwipe.model.Story;
import com.PBO.TaleSwipe.model.User;
import com.PBO.TaleSwipe.model.UserStoryInteraction;

@Repository
public interface UserStoryInteractionRepository extends JpaRepository<UserStoryInteraction, UUID> {
    List<UserStoryInteraction> findByUser(User user);
    Optional<UserStoryInteraction> findByUserAndStory(User user, Story story);
}