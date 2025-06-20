package com.PBO.TaleSwipe.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.PBO.TaleSwipe.dto.CommentRequest;
import com.PBO.TaleSwipe.dto.CommentResponse;
import com.PBO.TaleSwipe.model.Comment;
import com.PBO.TaleSwipe.model.Story;
import com.PBO.TaleSwipe.model.User;
import com.PBO.TaleSwipe.repository.CommentRepository;
import com.PBO.TaleSwipe.repository.StoryRepository;
import com.PBO.TaleSwipe.repository.UserRepository;
import com.PBO.TaleSwipe.service.CommentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;

    @Override
    public CommentResponse createComment(CommentRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Story story = storyRepository.findById(request.getStoryId())
                .orElseThrow(() -> new RuntimeException("Story not found"));

        Comment.CommentBuilder builder = Comment.builder()
                .commentText(request.getCommentText())
                .user(user)
                .story(story);

        // Tambahkan jika ada parent (reply)
        if (request.getParentCommentId() != null) {
            Comment parent = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            builder.parentComment(parent);
        }

        Comment savedComment = commentRepository.save(builder.build());
        return mapToResponse(savedComment, user);
    }

    // Flat (tidak nested)
    @Override
    public List<CommentResponse> getCommentsByStory(String storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found"));
        return commentRepository.findByStory(story).stream()
                .map(c -> mapToResponse(c, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponse> getCommentsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return commentRepository.findByUser(user).stream()
                .map(c -> mapToResponse(c, user))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void likeComment(String commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        comment.getLikedBy().add(user);
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void unlikeComment(String commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        comment.getLikedBy().remove(user);
        commentRepository.save(comment);
    }

    // Nested replies (recursive)
    @Override
    public List<CommentResponse> getCommentsByStoryNested(String storyId, String currentUsername) {
        Story story = storyRepository.findById(storyId)
            .orElseThrow(() -> new RuntimeException("Story not found"));
        List<Comment> topLevelComments = commentRepository.findByStoryAndParentCommentIsNull(story);
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElse(null); // final/effectively final karena tidak diubah setelahnya

        return topLevelComments.stream()
                .map(c -> mapToResponseRecursive(c, currentUser))
                .collect(Collectors.toList());
        }

    @Override
    @Transactional
    public void deleteComment(String commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Not authorized to delete this comment");
        }
        commentRepository.delete(comment);
    }

    // ======= FLAT RESPONSE (tanpa replies recursive) =======
    private CommentResponse mapToResponse(Comment comment, User currentUser) {
        boolean likedByCurrentUser = false;
        if (currentUser != null && comment.getLikedBy() != null) {
            likedByCurrentUser = comment.getLikedBy().contains(currentUser);
        }
        return CommentResponse.builder()
            .commentId(comment.getCommentId())
            .commentText(comment.getCommentText())
            .username(comment.getUser().getUsername())
            .userProfilePicture(comment.getUser().getProfilePicture())
            .displayName(comment.getUser().getName())
            .storyId(comment.getStory().getStoryId())
            .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null)
            .likeCount(comment.getLikedBy() != null ? comment.getLikedBy().size() : 0)
            .likedByCurrentUser(likedByCurrentUser)
            .replies(new ArrayList<>()) // flat, tidak ada nested
            .build();
    }

    // ======= NESTED (recursive replies) =======
    private CommentResponse mapToResponseRecursive(Comment comment, User currentUser) {
        boolean likedByCurrentUser = false;
        if (currentUser != null && comment.getLikedBy() != null) {
            likedByCurrentUser = comment.getLikedBy().contains(currentUser);
        }
        List<CommentResponse> replies = comment.getReplies() != null
                ? comment.getReplies().stream()
                    .map(reply -> mapToResponseRecursive(reply, currentUser))
                    .collect(Collectors.toList())
                : new ArrayList<>();
        return CommentResponse.builder()
            .commentId(comment.getCommentId())
            .commentText(comment.getCommentText())
            .username(comment.getUser().getUsername())
            .userProfilePicture(comment.getUser().getProfilePicture())
            .displayName(comment.getUser().getName())
            .storyId(comment.getStory().getStoryId())
            .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null)
            .likeCount(comment.getLikedBy() != null ? comment.getLikedBy().size() : 0)
            .likedByCurrentUser(likedByCurrentUser)
            .replies(replies)
            .build();
    }
}
