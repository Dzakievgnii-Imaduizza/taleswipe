package com.PBO.TaleSwipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
    private String storyId;
    private String commentText;
    private String parentCommentId;
} 
