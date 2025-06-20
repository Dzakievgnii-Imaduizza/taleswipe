package com.PBO.TaleSwipe.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.PBO.TaleSwipe.model.Story;
import com.PBO.TaleSwipe.model.User;

@Repository
public interface StoryRepository extends JpaRepository<Story, String> {

    List<Story> findByAuthor(User author);

    List<Story> findByGenresContaining(String genre);

    List<Story> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT s FROM Story s WHERE s.storyId = :storyId AND s.author.username = :username")
    Optional<Story> findStoryByIdAndAuthorUsername(
        @Param("storyId") String storyId,
        @Param("username") String username
    );

    Optional<Story> findByStoryId(String storyId);

    // ✅ Optimal: Ambil semua data cerita beserta likes, comments, pages, dan user-likenya
    @Query("SELECT DISTINCT s FROM Story s " +
           "LEFT JOIN FETCH s.likes l " +
           "LEFT JOIN FETCH s.bookmarkedBy " +
           "LEFT JOIN FETCH l.user " +
           "LEFT JOIN FETCH s.comments " +
           "LEFT JOIN FETCH s.pages " +
           "LEFT JOIN FETCH s.author")
    List<Story> findAllWithDetails();

    // Untuk StoryRepository
    @Query("SELECT s FROM Story s LEFT JOIN FETCH s.bookmarkedBy WHERE s.storyId = :storyId")
    Optional<Story> findByIdWithBookmarks(@Param("storyId") String storyId);
    List<Story> findByAuthorId(UUID authorId);
    
}
