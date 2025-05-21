// src/main/java/com/taleswipe/controller/CommentController.java
package com.taleswipe.controller;

import com.taleswipe.dto.request.CommentRequest;
import com.taleswipe.model.Comment;
import com.taleswipe.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.addComment(request));
    }

    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long chapterId) {
        return ResponseEntity.ok(commentService.getCommentsByChapter(chapterId));
    }
}
