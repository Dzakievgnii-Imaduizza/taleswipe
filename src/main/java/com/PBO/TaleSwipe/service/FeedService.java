package com.PBO.TaleSwipe.service;

import java.util.List;

import com.PBO.TaleSwipe.dto.StoryInteractionRequest;
import com.PBO.TaleSwipe.dto.StoryResponse;

public interface FeedService {

    List<StoryResponse> getRecommendedFeed(String username);
    List<StoryResponse> getFeed(int offset, int limit);

    void recordInteraction(String username, StoryInteractionRequest request);
}
