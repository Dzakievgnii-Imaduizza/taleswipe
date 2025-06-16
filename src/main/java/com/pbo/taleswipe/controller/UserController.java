package com.PBO.TaleSwipe.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.PBO.TaleSwipe.dto.response.ApiResponse;
import com.PBO.TaleSwipe.dto.response.ErrorResponse;
import com.PBO.TaleSwipe.dto.response.UserResponse;
import com.PBO.TaleSwipe.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody RegisterRequest request) {
        System.out.println(">>> MASUK REGISTER CONTROLLER");
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.getAllUsers(PageRequest.of(page - 1, size)));
    }

    @GetMapping("/{userId}")
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

    @DeleteMapping("/{username}")
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
    
    @PutMapping("/{userId}/profile-picture")
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

    @PutMapping("/{userId}/update")
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


}
