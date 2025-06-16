package com.PBO.TaleSwipe.service.impl;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.PBO.TaleSwipe.dto.PageResponse;
import com.PBO.TaleSwipe.dto.StoryInteractionRequest;
import com.PBO.TaleSwipe.dto.StoryResponse;
import com.PBO.TaleSwipe.model.Story;
import com.PBO.TaleSwipe.model.User;
import com.PBO.TaleSwipe.model.UserStoryInteraction;
import com.PBO.TaleSwipe.repository.StoryRepository;
import com.PBO.TaleSwipe.repository.UserRepository;
import com.PBO.TaleSwipe.repository.UserStoryInteractionRepository;
import com.PBO.TaleSwipe.service.FeedService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final UserRepository userRepository;
    private final StoryRepository storyRepository;
    private final UserStoryInteractionRepository interactionRepository;

    @Override
    public List<StoryResponse> getRecommendedFeed(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> preferredGenres = user.getPreferredGenres();
        List<Story> stories = storyRepository.findAll();

        return stories.stream()
                .filter(s -> s.getGenres().stream().anyMatch(preferredGenres::contains))
                .sorted(Comparator.comparing((Story s) -> s.getLikes().size()).reversed())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void recordInteraction(String username, StoryInteractionRequest request) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Story story = storyRepository.findById(request.getStoryId().toString())
            .orElseThrow(() -> new RuntimeException("Story not found"));

        UserStoryInteraction interaction = interactionRepository.findByUserAndStory(user, story)
            .orElse(UserStoryInteraction.builder().user(user).story(story).build());

        interaction.setLiked(request.isLiked());
        interaction.setBookmarked(request.isBookmarked());
        interaction.setReadDuration(request.getReadDuration());
        interaction.setInteractedAt(LocalDateTime.now());

        interactionRepository.save(interaction);
    }

    private StoryResponse toResponse(Story story) {
        return StoryResponse.builder()
                .storyId(story.getStoryId())
                .title(story.getTitle())
                .description(story.getDescription())
                .genres(story.getGenres())
                .pages(story.getPages().stream()
                        .map(p -> PageResponse.builder()
                                .pageId(p.getPageId())
                                .pageNumber(p.getPageNumber())
                                .content(p.getContent())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
