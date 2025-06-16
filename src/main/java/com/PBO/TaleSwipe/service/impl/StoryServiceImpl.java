package com.PBO.TaleSwipe.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.PBO.TaleSwipe.dto.PageResponse;
import com.PBO.TaleSwipe.dto.StoryRequest;
import com.PBO.TaleSwipe.dto.StoryResponse;
import com.PBO.TaleSwipe.model.Like;
import com.PBO.TaleSwipe.model.Page;
import com.PBO.TaleSwipe.model.Story;
import com.PBO.TaleSwipe.model.User;
import com.PBO.TaleSwipe.repository.StoryRepository;
import com.PBO.TaleSwipe.repository.UserRepository;
import com.PBO.TaleSwipe.service.StoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoryServiceImpl implements StoryService {

    private final StoryRepository storyRepository;
    private final UserRepository userRepository;

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

        // Menambahkan halaman
        int pageNumber = 1; // Mulai dari halaman 1
        for (String content : request.getPageContents()) {
                Page page = Page.builder()
                        .content(content)
                        .pageNumber(pageNumber++) // Menetapkan nomor halaman bertambah
                        .story(story)
                        .build();
                story.getPages().add(page);
        }

        Story savedStory = storyRepository.save(story);
        return mapToResponse(savedStory, username);
        }


        @Override
        public StoryResponse getStory(String storyId, String username) {
        System.out.println("? Mencari cerita dengan ID: " + storyId + " oleh user: " + username);

        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> {
                        System.out.println("❌ Cerita tidak ditemukan.");
                        return new RuntimeException("Story not found");
                });

        return mapToResponse(story, username);
        }





    @Override
    public List<StoryResponse> getAllStories(String username) {
        return storyRepository.findAll().stream()
                .map(story -> mapToResponse(story, username))
                .collect(Collectors.toList());
    }

    @Override
    public List<StoryResponse> getStoriesByAuthor(String authorUsername, String currentUsername) {
        User author = userRepository.findByUsername(authorUsername)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        return storyRepository.findByAuthor(author).stream()
                .map(story -> mapToResponse(story, currentUsername))
                .collect(Collectors.toList());
    }

    @Override
    public List<StoryResponse> getStoriesByGenre(String genre, String username) {
        return storyRepository.findByGenresContaining(genre).stream()
                .map(story -> mapToResponse(story, username))
                .collect(Collectors.toList());
    }

    @Override
    public List<StoryResponse> searchStories(String query, String username) {
        return storyRepository.findByTitleContainingIgnoreCase(query).stream()
                .map(story -> mapToResponse(story, username))
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
        // Ambil story berdasarkan ID dan username author
        Story story = storyRepository.findStoryByIdAndAuthorUsername(storyId, username)
                .orElseThrow(() -> new RuntimeException("Story not found or not owned by user"));

        // Update field dasar
        story.setTitle(request.getTitle());
        story.setDescription(request.getDescription());
        story.setGenres(request.getGenres());

        // Hapus semua halaman lama dari koleksi yang sudah dikelola JPA
        story.getPages().clear();

        // Tambahkan halaman baru langsung ke koleksi
        int pageNumber = 1;
        for (String content : request.getPageContents()) {
                Page newPage = Page.builder()
                        .content(content)
                        .pageNumber(pageNumber++)
                        .story(story)
                        .build();
                story.getPages().add(newPage); // langsung ke list original, bukan pakai setPages()
        }

        // Simpan perubahan
        Story updatedStory = storyRepository.save(story);

        return mapToResponse(updatedStory, username);
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

        // Hibernate akan otomatis menghapus page karena orphanRemoval = true
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

    private StoryResponse mapToResponse(Story story, String currentUsername) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int likesCount = (story.getLikes() != null) ? story.getLikes().size() : 0;
        int commentsCount = (story.getComments() != null) ? story.getComments().size() : 0;

        boolean isLiked = story.getLikes().stream()
                .anyMatch(like -> like.getUser().equals(currentUser));

        return StoryResponse.builder()
                .storyId(story.getStoryId())
                .title(story.getTitle())
                .description(story.getDescription())  // Menggunakan description
                .genres(story.getGenres())
                .authorUsername(story.getAuthor().getUsername())
                .likesCount(likesCount)
                .commentsCount(commentsCount)
                .isLikedByCurrentUser(isLiked)
                .pages(story.getPages().stream()
                        .map(page -> PageResponse.builder()
                                .pageId(page.getPageId())
                                .pageNumber(page.getPageNumber())
                                .content(page.getContent())
                                .build())
                        .collect(Collectors.toList()))  // Menambahkan halaman pada response
                .build();
    }
    
}
