package com.PBO.TaleSwipe.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.PBO.TaleSwipe.dto.PageResponse;
import com.PBO.TaleSwipe.dto.StoryRequest;
import com.PBO.TaleSwipe.dto.StoryResponse;
import com.PBO.TaleSwipe.service.StoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stories")
@RequiredArgsConstructor
public class StoryController {
    private final StoryService storyService;

    private String getUsernameFromContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

@PostMapping
public ResponseEntity<StoryResponse> createStory(@RequestBody StoryRequest request) {
    return ResponseEntity.ok(storyService.createStory(request, getUsernameFromContext()));
}

    @GetMapping("/{storyId}")
    public ResponseEntity<StoryResponse> getStory(@PathVariable String storyId) {
        try {
            return ResponseEntity.ok(storyService.getStory(storyId, getUsernameFromContext()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<StoryResponse>> getAllStories() {
        return ResponseEntity.ok(storyService.getAllStories(getUsernameFromContext()));
    }

    @GetMapping("/author/{username}")
    public ResponseEntity<List<StoryResponse>> getStoriesByAuthor(@PathVariable String username) {
        return ResponseEntity.ok(storyService.getStoriesByAuthor(username, getUsernameFromContext()));
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<StoryResponse>> getStoriesByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(storyService.getStoriesByGenre(genre, getUsernameFromContext()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<StoryResponse>> searchStories(@RequestParam String query) {
        return ResponseEntity.ok(storyService.searchStories(query, getUsernameFromContext()));
    }

    @PostMapping("/{storyId}/like")
    public ResponseEntity<Void> likeStory(@PathVariable String storyId) {
        storyService.likeStory(storyId, getUsernameFromContext());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{storyId}/like")
    public ResponseEntity<Void> unlikeStory(@PathVariable String storyId) {
        storyService.unlikeStory(storyId, getUsernameFromContext());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{storyId}")
    public ResponseEntity<Void> deleteStory(@PathVariable String storyId) {
        storyService.deleteStory(storyId, getUsernameFromContext());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{storyId}/pages")
    public ResponseEntity<List<PageResponse>> getPages(@PathVariable String storyId) {
        return ResponseEntity.ok(storyService.getPages(storyId));
    }
    @GetMapping("/{storyId}/pages/{pageNumber}")
    public ResponseEntity<PageResponse> getPageByNumber(
            @PathVariable String storyId,
            @PathVariable int pageNumber) {
        return ResponseEntity.ok(storyService.getPageByNumber(storyId, pageNumber));
    }

    @PutMapping("/{storyId}")
    public ResponseEntity<StoryResponse> updateStory(
            @PathVariable String storyId,
            @RequestBody StoryRequest request) {
        try {
            StoryResponse updated = storyService.updateStory(storyId, request, getUsernameFromContext());
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) { // ⬅️ Tambahkan ini agar log exception muncul
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // sementara ubah jadi 500
        }
    }

    @DeleteMapping("/{storyId}/pages/{pageId}")
    public ResponseEntity<Void> deletePage(
        @PathVariable String storyId,
        @PathVariable String pageId) {
    storyService.deletePage(storyId, pageId, getUsernameFromContext());
    return ResponseEntity.ok().build();
}


}
