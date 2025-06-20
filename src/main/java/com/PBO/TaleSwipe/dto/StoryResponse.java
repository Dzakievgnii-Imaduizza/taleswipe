package com.PBO.TaleSwipe.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
@AllArgsConstructor
public class StoryResponse {
    private UUID authorId;
    private String storyId;
    private String title;
    private String description;
    private String coverUrl;
    private List<PageResponse> pages;
    private AuthorResponse author;
    private LikeBookmarkCountResponse reactions;
    private List<CommentResponse> comments;
    private List<String> genres;
    private boolean likedByCurrentUser;      // <--
    private boolean bookmarkedByCurrentUser; // <--
    private LocalDateTime createdAt;         // <--
    private int commentCount;
    private boolean ByCurrentUser;
    private UUID currentUserId;
}

