package com.PBO.TaleSwipe.service;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.PBO.TaleSwipe.dto.PageResponse;
import com.PBO.TaleSwipe.dto.StoryRequest;
import com.PBO.TaleSwipe.dto.StoryResponse;

public interface StoryService {
    StoryResponse createStory(StoryRequest request, String username);
    StoryResponse getStory(String storyId, String username);

    /**
     * Optimized version to load all stories with author, pages, comments.user, and likes.
     */
    List<StoryResponse> getAllStories(String username);

    List<StoryResponse> getStoriesByAuthor(String authorUsername, String currentUsername);
    List<StoryResponse> getStoriesByGenre(String genre, String username);
    List<StoryResponse> searchStories(String query, String username);
    List<PageResponse> getPages(String storyId);
    PageResponse getPageByNumber(String storyId, int pageNumber);
    void likeStory(String storyId, String username);
    void unlikeStory(String storyId, String username);
    void deleteStory(String storyId, String username);
    StoryResponse updateStory(String storyId, StoryRequest request, String username);
    void deletePage(String storyId, String pageId, String username);
    String uploadCoverImage(UUID storyId, MultipartFile file);
    void bookmarkStory(String storyId, String username);
    void unbookmarkStory(String storyId, String username);
    List<StoryResponse> getBookmarkedStories(String username);
    List<StoryResponse> getStoriesByUserId(UUID userId, String currentUsername);
    StoryResponse updateStoryWithCover(
        String storyId,
        String title,
        String description,
        List<String> genres,
        List<String> pageContents,
        MultipartFile cover,
        String username
    );
}
