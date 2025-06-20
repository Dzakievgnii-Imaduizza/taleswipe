package com.PBO.TaleSwipe.service.impl;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID; 
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.PBO.TaleSwipe.dto.AuthorResponse;
import com.PBO.TaleSwipe.dto.CommentResponse;
import com.PBO.TaleSwipe.dto.LikeBookmarkCountResponse;
import com.PBO.TaleSwipe.dto.PageResponse;
import com.PBO.TaleSwipe.dto.StoryRequest;
import com.PBO.TaleSwipe.dto.StoryResponse;
import com.PBO.TaleSwipe.model.Like;
import com.PBO.TaleSwipe.model.Page;
import com.PBO.TaleSwipe.model.Story;
import com.PBO.TaleSwipe.model.User;
import com.PBO.TaleSwipe.repository.StoryRepository;
import com.PBO.TaleSwipe.repository.UserRepository;
import com.PBO.TaleSwipe.service.FileStorageService;
import com.PBO.TaleSwipe.service.StoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoryServiceImpl implements StoryService {

    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public StoryResponse createStory(StoryRequest request, String username) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Story story = Story.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .genres(request.getGenres())
                .author(author)
                .build();

        int pageNumber = 1;
        for (String content : request.getPageContents()) {
            Page page = Page.builder()
                    .content(content)
                    .pageNumber(pageNumber++)
                    .story(story)
                    .build();
            story.getPages().add(page);
        }

        Story savedStory = storyRepository.save(story);
        return mapToResponse(savedStory, author);
    }

    @Override
    public StoryResponse getStory(String storyId, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found"));
        return mapToResponse(story, currentUser);
    }

    @Override
    public List<StoryResponse> getStoriesByGenre(String genre, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return storyRepository.findByGenresContaining(genre).stream()
                .map(story -> mapToResponse(story, currentUser))
                .collect(Collectors.toList());
    }

    @Override
    public List<StoryResponse> searchStories(String query, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return storyRepository.findByTitleContainingIgnoreCase(query).stream()
                .map(story -> mapToResponse(story, currentUser))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void likeStory(String storyId, String username) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean alreadyLiked = story.getLikes().stream()
                .anyMatch(like -> like.getUser().equals(user));

        if (!alreadyLiked) {
            Like like = Like.builder()
                    .user(user)
                    .story(story)
                    .build();
            story.getLikes().add(like);
            storyRepository.save(story);
        }
    }

    @Override
    @Transactional
    public StoryResponse updateStory(String storyId, StoryRequest request, String username) {
        Story story = storyRepository.findStoryByIdAndAuthorUsername(storyId, username)
                .orElseThrow(() -> new RuntimeException("Story not found or not owned by user"));

        story.setTitle(request.getTitle());
        story.setDescription(request.getDescription());
        story.setGenres(request.getGenres());
        story.getPages().clear();

        int pageNumber = 1;
        for (String content : request.getPageContents()) {
            Page newPage = Page.builder()
                    .content(content)
                    .pageNumber(pageNumber++)
                    .story(story)
                    .build();
            story.getPages().add(newPage);
        }

        Story updatedStory = storyRepository.save(story);
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(updatedStory, currentUser);
    }
    @Override
    @Transactional
public StoryResponse updateStoryWithCover(
        String storyId,
        String title,
        String description,
        List<String> genres,
        List<String> pageContents,
        MultipartFile cover,
        String username
) {
    Story story = storyRepository.findStoryByIdAndAuthorUsername(storyId, username)
            .orElseThrow(() -> new RuntimeException("Story not found or not owned by user"));

    story.setTitle(title);
    story.setDescription(description);
    story.setGenres(genres);

    // Update halaman
    story.getPages().clear();
    int pageNumber = 1;
    for (String content : pageContents) {
        Page newPage = Page.builder()
                .content(content)
                .pageNumber(pageNumber++)
                .story(story)
                .build();
        story.getPages().add(newPage);
    }

    // Jika ada file cover baru, simpan
    if (cover != null && !cover.isEmpty()) {
        String uploadDir = "uploads/story_covers/";
        Path uploadPath = Paths.get(System.getProperty("user.dir")).resolve(uploadDir);
        try {
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            String filename = "story_" + storyId + "_" + cover.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            cover.transferTo(filePath.toFile());
            story.setCoverUrl("/" + uploadDir + filename);
        } catch (IOException e) {
    throw new RuntimeException("Gagal upload cover", e);
}
    }

    Story updatedStory = storyRepository.save(story);
    User currentUser = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    return mapToResponse(updatedStory, currentUser);
}

    @Override
    @Transactional
    public void deletePage(String storyId, String pageId, String username) {
        Story story = storyRepository.findStoryByIdAndAuthorUsername(storyId, username)
                .orElseThrow(() -> new RuntimeException("Story not found or not owned by user"));

        boolean removed = story.getPages().removeIf(page -> page.getPageId().equals(pageId));
        if (!removed) {
            throw new RuntimeException("Page not found or does not belong to this story");
        }
        storyRepository.save(story);
    }

    @Override
    @Transactional
    public void unlikeStory(String storyId, String username) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        story.getLikes().removeIf(like -> like.getUser().equals(user));
        storyRepository.save(story);
    }

    @Override
    @Transactional
    public void deleteStory(String storyId, String username) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found"));

        if (!story.getAuthor().getUsername().equals(username)) {
            throw new RuntimeException("Not authorized to delete this story");
        }
        storyRepository.delete(story);
    }

    @Override
    public List<PageResponse> getPages(String storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found"));

        return story.getPages().stream()
                .map(page -> PageResponse.builder()
                        .pageId(page.getPageId())
                        .pageNumber(page.getPageNumber())
                        .content(page.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String uploadCoverImage(UUID storyId, MultipartFile file) {
        Optional<Story> optionalStory = storyRepository.findById(storyId.toString());
        if (optionalStory.isEmpty()) {
            throw new RuntimeException("Story tidak ditemukan dengan ID: " + storyId);
        }

        Story story = optionalStory.get();
        String fileName = "story_" + storyId + "_" + file.getOriginalFilename();
        String imageUrl = fileStorageService.storeFile(file, fileName);
        story.setCoverUrl(imageUrl);
        storyRepository.save(story);

        return imageUrl;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoryResponse> getAllStories(String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return storyRepository.findAllWithDetails().stream()
                .map(story -> mapToResponse(story, currentUser))
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse getPageByNumber(String storyId, int pageNumber) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found"));

        return story.getPages().stream()
                .filter(page -> page.getPageNumber() == pageNumber)
                .findFirst()
                .map(page -> PageResponse.builder()
                        .pageId(page.getPageId())
                        .pageNumber(page.getPageNumber())
                        .content(page.getContent())
                        .build())
                .orElseThrow(() -> new RuntimeException("Page not found"));
    }

    // TIDAK pakai @Override jika tidak dideklarasikan di interface!
    @Override
    public List<StoryResponse> getStoriesByUserId(UUID userId, String currentUsername) {
        List<Story> stories = storyRepository.findByAuthorId(userId);
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null);
        return stories.stream()
                .map(story -> mapToResponse(story, currentUser))
                .collect(Collectors.toList());
    }

    @Override
    public List<StoryResponse> getStoriesByAuthor(String authorUsername, String currentUsername) {
        User author = userRepository.findByUsername(authorUsername)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return storyRepository.findByAuthor(author).stream()
                .map(story -> mapToResponse(story, currentUser))
                .collect(Collectors.toList());
    }

    @Override
    public void bookmarkStory(String storyId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found"));
        story.getBookmarkedBy().add(user);
        storyRepository.save(story);
    }

    @Override
    public void unbookmarkStory(String storyId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found"));
        story.getBookmarkedBy().remove(user);
        storyRepository.save(story);
    }

    @Override
    public List<StoryResponse> getBookmarkedStories(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Set<Story> bookmarks = user.getBookmarkedStories();

        return bookmarks.stream()
                .map(story -> mapToResponse(story, user))
                .collect(Collectors.toList());
    }

    // Tidak perlu @Override karena ini hanya helper private
    private StoryResponse mapToResponse(Story story, User currentUser) {
        // Author mapping
        AuthorResponse authorResponse = AuthorResponse.builder()
            .username(story.getAuthor().getUsername())
            .name(story.getAuthor().getName())
            .profilePicture(story.getAuthor().getProfilePicture())
            .followerCount(story.getAuthor().getFollowers() != null ? story.getAuthor().getFollowers().size() : 0)
            .followedByCurrentUser(
                currentUser != null &&
                story.getAuthor().getFollowers() != null &&
                story.getAuthor().getFollowers().contains(currentUser)
            )
            .build();

        // Pages mapping
        List<PageResponse> pages = story.getPages().stream()
            .map(page -> PageResponse.builder()
                    .pageId(page.getPageId())
                    .pageNumber(page.getPageNumber())
                    .content(page.getContent())
                    .build())
            .collect(Collectors.toList());

        // Comments mapping
        List<CommentResponse> comments = story.getComments().stream()
            .map(comment -> CommentResponse.builder()
                    .commentId(comment.getCommentId())
                    .commentText(comment.getCommentText())
                    .username(comment.getUser().getUsername())
                    .build())
            .collect(Collectors.toList());

        // Reactions
        LikeBookmarkCountResponse reactions = LikeBookmarkCountResponse.builder()
            .likeCount(story.getLikes() != null ? story.getLikes().size() : 0)
            .bookmarkCount(story.getBookmarkedBy() != null ? story.getBookmarkedBy().size() : 0)
            .build();

        boolean likedByCurrentUser = currentUser != null && story.getLikes().stream()
            .anyMatch(like -> like.getUser().equals(currentUser));
        boolean bookmarkedByCurrentUser = currentUser != null && story.getBookmarkedBy().contains(currentUser);

        return StoryResponse.builder()
            .currentUserId(currentUser != null ? currentUser.getId() : null)
            .storyId(story.getStoryId())
            .title(story.getTitle())
            .description(story.getDescription())
            .coverUrl(story.getCoverUrl())
            .pages(pages)
            .author(authorResponse)
            .reactions(reactions)
            .comments(comments)
            .genres(story.getGenres())
            .likedByCurrentUser(likedByCurrentUser)
            .bookmarkedByCurrentUser(bookmarkedByCurrentUser)
            .createdAt(story.getCreatedAt())
            .commentCount(story.getComments() != null ? story.getComments().size() : 0)
            .build();
    }
}
