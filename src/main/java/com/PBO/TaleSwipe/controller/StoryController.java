package com.PBO.TaleSwipe.controller;

import java.util.List;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.PBO.TaleSwipe.dto.PageResponse;
import com.PBO.TaleSwipe.dto.StoryRequest;
import com.PBO.TaleSwipe.dto.StoryResponse;
import com.PBO.TaleSwipe.dto.response.ApiResponse;
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

    // ✅ GET all stories with optimized relations

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<StoryResponse>> getStoriesByUserId(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(storyService.getStoriesByUserId(userId, getUsernameFromContext()));
    }

    @GetMapping
    public ResponseEntity<List<StoryResponse>> getAllStories() {
        return ResponseEntity.ok(storyService.getAllStories(getUsernameFromContext()));
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

// ... (endpoint lain)

@PutMapping(value = "/{storyId}", consumes = {"multipart/form-data"})
public ResponseEntity<StoryResponse> updateStoryWithCover(
        @PathVariable String storyId,
        @RequestParam("title") String title,
        @RequestParam("description") String description,
        @RequestParam("genres") List<String> genres,
        @RequestParam("pageContents") List<String> pageContents,
        @RequestPart(value = "cover", required = false) MultipartFile cover,
        Authentication authentication
) {
    String username = authentication.getName();
    StoryResponse updated = storyService.updateStoryWithCover(
        storyId, title, description, genres, pageContents, cover, username
    );
    return ResponseEntity.ok(updated);
}

@PutMapping(value = "/{storyId}", consumes = {"application/json"})
public ResponseEntity<StoryResponse> updateStoryJson(
        @PathVariable String storyId,
        @RequestBody StoryRequest request,
        Authentication authentication
) {
    String username = authentication.getName();
    StoryResponse updated = storyService.updateStory(storyId, request, username);
    return ResponseEntity.ok(updated);
}


    @DeleteMapping("/{storyId}/pages/{pageId}")
    public ResponseEntity<Void> deletePage(
            @PathVariable String storyId,
            @PathVariable String pageId) {
        storyService.deletePage(storyId, pageId, getUsernameFromContext());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{storyId}/cover")
    public ResponseEntity<ApiResponse<String>> uploadCover(
            @PathVariable UUID storyId,
            @RequestParam("file") MultipartFile file) {
        String imageUrl = storyService.uploadCoverImage(storyId, file);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "OK",
                "Cover berhasil diunggah",
                imageUrl
        ));
    }

    
    @PostMapping("/{storyId}/bookmark")
    public ResponseEntity<Void> bookmarkStory(@PathVariable String storyId) {
        storyService.bookmarkStory(storyId, getUsernameFromContext());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{storyId}/bookmark")
    public ResponseEntity<Void> unbookmarkStory(@PathVariable String storyId) {
        storyService.unbookmarkStory(storyId, getUsernameFromContext());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/author/{username}")
    public ResponseEntity<List<StoryResponse>> getStoriesByAuthor(
            @PathVariable String username) {
        return ResponseEntity.ok(storyService.getStoriesByAuthor(username, getUsernameFromContext()));
    }
    @GetMapping("/bookmarks/me")
    public ResponseEntity<List<StoryResponse>> getBookmarkedStoriesOfCurrentUser() {
        return ResponseEntity.ok(storyService.getBookmarkedStories(getUsernameFromContext()));
    }

    // StoryController.java

    @GetMapping("/bookmarked")
    public ResponseEntity<List<StoryResponse>> getBookmarkedStories() {
        String username = getUsernameFromContext();
        return ResponseEntity.ok(storyService.getBookmarkedStories(username));
    }


}
