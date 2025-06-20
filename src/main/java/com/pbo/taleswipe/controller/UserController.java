package com.PBO.TaleSwipe.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.PBO.TaleSwipe.dto.LoginRequest;
import com.PBO.TaleSwipe.dto.PreferenceRequest;
import com.PBO.TaleSwipe.dto.RegisterRequest;
import com.PBO.TaleSwipe.dto.StoryResponse;
import com.PBO.TaleSwipe.dto.response.ApiResponse;
import com.PBO.TaleSwipe.dto.response.ErrorResponse;
import com.PBO.TaleSwipe.dto.response.UserResponse;
import com.PBO.TaleSwipe.service.StoryService;
import com.PBO.TaleSwipe.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final StoryService storyService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody RegisterRequest request) {
        System.out.println(">>> MASUK REGISTER CONTROLLER");
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication authentication) {
        String currentUsername = authentication.getName();
        UserResponse resp = userService.getUserProfile(currentUsername, currentUsername);
        return ResponseEntity.ok(
            ApiResponse.success(200, "OK", "Current user profile fetched", resp)
        );
    }

    // Mendapatkan daftar user yang di-follow oleh user dengan userId tertentu
    @GetMapping("/by-id/{userId}/following")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getFollowingByUserId(@PathVariable UUID userId) {
        List<UserResponse> followingList = userService.getFollowingByUserId(userId);
        return ResponseEntity.ok(
            ApiResponse.success(200, "OK", "Following list fetched successfully", followingList)
        );
    }


    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.getAllUsers(PageRequest.of(page - 1, size)));
    }

    // GET user by UUID
    @GetMapping("/by-id/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID userId) {
        try {
            return ResponseEntity.ok(userService.getUserById(userId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(
                            HttpStatus.NOT_FOUND.value(),
                            "NOT_FOUND",
                            "User not found",
                            ErrorResponse.builder()
                                    .name("User")
                                    .message("User not found")
                                    .build()
                    ));
        }
    }

    // GET user by username (safe, readable, non-ambiguous)
    @GetMapping("/by-username/{username}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(
            @PathVariable String username,
            Authentication auth) {
        String currentUsername = auth != null ? auth.getName() : null;
        UserResponse resp = userService.getUserProfile(username, currentUsername);
        return ResponseEntity.ok(
            ApiResponse.success(200, "OK", "User found", resp)
        );
    }

    // DELETE user by username (safe, readable, non-ambiguous)
    @DeleteMapping("/by-username/{username}")
    public ResponseEntity<ApiResponse<String>> deleteUserByUsername(@PathVariable String username) {
        try {
            userService.deleteByUsername(username);
            return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "OK",
                "User berhasil dihapus",
                "DELETED"
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(
                            HttpStatus.NOT_FOUND.value(),
                            "NOT_FOUND",
                            "User tidak ditemukan",
                            ErrorResponse.builder()
                                    .name("User")
                                    .message("Tidak ditemukan user dengan username: " + username)
                                    .build()
                    ));
        }
    }

    // Upload profile picture by userId (UUID)
    @PutMapping("/by-id/{userId}/profile-picture")
    public ResponseEntity<ApiResponse<String>> uploadProfilePicture(
            @PathVariable UUID userId,
            @RequestParam("file") MultipartFile file) {
        String imageUrl = userService.uploadProfilePicture(userId, file);
        return ResponseEntity.ok(ApiResponse.success(
            HttpStatus.OK.value(),
            "OK",
            "Foto profil berhasil diunggah",
            imageUrl
        ));
    }

    // Update user info by userId (UUID)
    @PutMapping("/by-id/{userId}/update")
    public ResponseEntity<ApiResponse<String>> updateUserInfo(
            @PathVariable UUID userId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String password) {
        try {
            userService.updateUserInfo(userId, name, password);
            return ResponseEntity.ok(ApiResponse.success(
                    HttpStatus.OK.value(),
                    "OK",
                    "Informasi pengguna berhasil diperbarui",
                    "UPDATED"
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(
                            HttpStatus.NOT_FOUND.value(),
                            "NOT_FOUND",
                            "User tidak ditemukan",
                            ErrorResponse.builder()
                                    .name("User")
                                    .message("Tidak ditemukan user dengan ID: " + userId)
                                    .build()
                    ));
        }
    }

    // User preferences
    @PostMapping("/preferences")
    public ResponseEntity<ApiResponse<String>> savePreferences(@RequestBody PreferenceRequest request) {
        userService.saveUserPreferences(request);
        return ResponseEntity.ok(ApiResponse.success(
            HttpStatus.OK.value(),
            "OK",
            "Preferensi genre disimpan",
            "OK"
        ));
    }

    // Follow/unfollow by username (username is safe for this context)
    @PostMapping("/{username}/follow")
    public ResponseEntity<?> followAuthor(@PathVariable String username, Authentication auth) {
        userService.followAuthor(auth.getName(), username);
        return ResponseEntity.ok().build();
    }

    

    @DeleteMapping("/{username}/follow")
    public ResponseEntity<?> unfollowAuthor(@PathVariable String username, Authentication auth) {
        userService.unfollowAuthor(auth.getName(), username);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/me/bookmarks")
    public ResponseEntity<ApiResponse<List<StoryResponse>>> getUserBookmarks(Authentication authentication) {
        String username = authentication.getName();
        List<StoryResponse> bookmarks = storyService.getBookmarkedStories(username); // Panggil storyService, bukan userService!
        return ResponseEntity.ok(ApiResponse.success(
            200, "OK", "Bookmark list fetched successfully", bookmarks
        ));
    }


}
