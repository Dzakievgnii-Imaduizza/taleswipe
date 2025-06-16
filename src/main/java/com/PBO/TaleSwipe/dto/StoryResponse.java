package com.PBO.TaleSwipe.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoryResponse {
    private String storyId;
    private String title;
    private String description; 
    private List<String> genres;
    private String authorUsername;
    private int likesCount;
    private int commentsCount;
    private boolean isLikedByCurrentUser;
    private List<PageResponse> pages;
} 
