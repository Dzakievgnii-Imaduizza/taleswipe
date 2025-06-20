package com.PBO.TaleSwipe.service.impl;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.PBO.TaleSwipe.dto.AuthorResponse;
import com.PBO.TaleSwipe.dto.CommentResponse;
import com.PBO.TaleSwipe.dto.LikeBookmarkCountResponse;
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
        // Build author DTO
        AuthorResponse authorResponse = AuthorResponse.builder()
            .username(story.getAuthor().getUsername())
            .name(story.getAuthor().getName())
            .profilePicture(story.getAuthor().getProfilePicture())
            .build();

        // Build list of pages (maksimal 4)
        List<PageResponse> pages = story.getPages().stream()
            .limit(4)
            .map(p -> PageResponse.builder()
                    .pageId(p.getPageId())
                    .pageNumber(p.getPageNumber())
                    .content(p.getContent())
                    .build())
            .collect(Collectors.toList());

        // Build list of comments
        List<CommentResponse> comments = story.getComments().stream()
            .map(c -> CommentResponse.builder()
                    .commentId(c.getCommentId())
                    .commentText(c.getCommentText())
                    .username(c.getUser().getUsername())
                    .build())
            .collect(Collectors.toList());

        // Buat StoryResponse final
        return StoryResponse.builder()
        .storyId(story.getStoryId())
        .title(story.getTitle())
        .description(story.getDescription())
        .coverUrl(story.getCoverUrl())
        .pages(pages)
        .author(authorResponse)
        .reactions(
            LikeBookmarkCountResponse.builder()
                .likeCount(story.getLikes().size())
                .bookmarkCount(0) // ganti logic jika perlu
                .build()
        )
        .comments(comments)
        .genres(story.getGenres())
        .build();
    }

    @Override
    public List<StoryResponse> getFeed(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by("storyId").descending());
        List<Story> stories = storyRepository.findAll(pageable).getContent();
        return stories.stream()
            .map(this::toResponse) // atau mapToResponse
            .collect(Collectors.toList());
}
}
