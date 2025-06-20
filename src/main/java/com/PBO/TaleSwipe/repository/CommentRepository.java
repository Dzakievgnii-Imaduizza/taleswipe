package com.PBO.TaleSwipe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.PBO.TaleSwipe.model.Comment;
import com.PBO.TaleSwipe.model.Story;
import com.PBO.TaleSwipe.model.User;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findByStoryAndParentCommentIsNull(Story story);
    List<Comment> findByUser(User user);
    List<Comment> findByStory(Story story);
} 
