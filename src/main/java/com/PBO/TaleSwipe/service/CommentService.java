package com.PBO.TaleSwipe.service;

import java.util.List;

import com.PBO.TaleSwipe.dto.CommentRequest;
import com.PBO.TaleSwipe.dto.CommentResponse;

public interface CommentService {
    void likeComment(String commentId, String username);
    void unlikeComment(String commentId, String username);
    List<CommentResponse> getCommentsByStoryNested(String storyId, String currentUsername);
    CommentResponse createComment(CommentRequest request, String username);
    List<CommentResponse> getCommentsByStory(String storyId);
    List<CommentResponse> getCommentsByUser(String username);
    void deleteComment(String commentId, String username);
} 
