package com.PBO.TaleSwipe.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.PBO.TaleSwipe.dto.CommentRequest;
import com.PBO.TaleSwipe.dto.CommentResponse;
import com.PBO.TaleSwipe.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @RequestBody CommentRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(commentService.createComment(request, authentication.getName()));
    }

    @GetMapping("/story/{storyId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByStory(
            @PathVariable String storyId) {
        return ResponseEntity.ok(commentService.getCommentsByStory(storyId));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<CommentResponse>> getCommentsByUser(
            @PathVariable String username) {
        return ResponseEntity.ok(commentService.getCommentsByUser(username));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable String commentId,
            Authentication authentication) {
        commentService.deleteComment(commentId, authentication.getName());
        return ResponseEntity.ok().build();
    }

    
}
