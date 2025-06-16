package com.PBO.TaleSwipe.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryInteractionRequest {
    private UUID storyId;
    private boolean liked;
    private boolean bookmarked;
    private long readDuration;
}