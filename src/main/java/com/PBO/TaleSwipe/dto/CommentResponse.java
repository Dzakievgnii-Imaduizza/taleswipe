package com.PBO.TaleSwipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private String commentId;
    private String commentText;
    private String username;
    private String storyId;
    private String parentCommentId;

} 