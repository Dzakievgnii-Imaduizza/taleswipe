package com.PBO.TaleSwipe.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.PBO.TaleSwipe.dto.LoginRequest;
import com.PBO.TaleSwipe.dto.PreferenceRequest;
import com.PBO.TaleSwipe.dto.RegisterRequest;
import com.PBO.TaleSwipe.dto.response.ApiResponse;
import com.PBO.TaleSwipe.dto.response.ErrorResponse;
import com.PBO.TaleSwipe.dto.response.PaginationResponse;
import com.PBO.TaleSwipe.dto.response.UserResponse;
import com.PBO.TaleSwipe.model.Role;
import com.PBO.TaleSwipe.model.User;
import com.PBO.TaleSwipe.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public ApiResponse<UserResponse> register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ApiResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "Validation failed",
                ErrorResponse.builder()
                    .name("Username")
                    .message("Username already exists")
                    .build()
            );
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return ApiResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "Validation failed",
                ErrorResponse.builder()
                    .name("Email")
                    .message("Email already exists")
                    .build()
            );
        }

        User user = User.builder()
                .username(request.getUsername())
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        user = userRepository.save(user);
        String token = jwtService.generateToken(user);

        UserResponse response = UserResponse.builder()
                .userId(user.getId().toString())
                .fullname(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .token(token)
                .preferredGenres(user.getPreferredGenres())
                .profilePicture(user.getProfilePicture())
                .build();

        return ApiResponse.success(
            HttpStatus.CREATED.value(),
            "CREATED",
            "User created successfully",
            response
        );
    }

    public String uploadProfilePicture(UUID userId, MultipartFile file) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        try {
            String uploadDir = "uploads/profile_pictures/";
            Path uploadPath = Paths.get(System.getProperty("user.dir")).resolve(uploadDir);

            // Buat direktori jika belum ada
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Hapus file lama
            if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
                Path oldPath = Paths.get(System.getProperty("user.dir"))
                    .resolve(user.getProfilePicture().replaceFirst("^/", ""));
                if (Files.exists(oldPath)) {
                    Files.delete(oldPath);
                }
            }

            // Simpan file baru
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            file.transferTo(filePath.toFile());

            // Simpan path relatif untuk frontend
            user.setProfilePicture("/" + uploadDir + filename);
            userRepository.save(user);

            return user.getProfilePicture();

        } catch (IOException e) {
            throw new RuntimeException("Gagal upload gambar", e);
        }
    }



    public ApiResponse<UserResponse> login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            String token = jwtService.generateToken(user);

            UserResponse response = UserResponse.builder()
                    .userId(user.getId().toString())
                    .fullname(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .token(token)
                    .preferredGenres(user.getPreferredGenres())
                    .profilePicture(user.getProfilePicture())
                    .build();

            return ApiResponse.success(
                HttpStatus.OK.value(),
                "OK",
                "Login successful",
                response
            );
        } catch (AuthenticationException e) {
            return ApiResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "Authentication failed",
                ErrorResponse.builder()
                    .name("Authentication")
                    .message("Invalid username or password")
                    .build()
            );
        }
    }

    public ApiResponse<List<UserResponse>> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        List<UserResponse> userResponses = userPage.getContent().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());

        PaginationResponse pagination = PaginationResponse.builder()
                .currentPage(userPage.getNumber() + 1)
                .totalPage(userPage.getTotalPages())
                .limit(userPage.getSize())
                .totalItem(userPage.getTotalElements())
                .build();

        return ApiResponse.successWithPagination(
            HttpStatus.OK.value(),
            "OK",
            "Paged users fetched successfully",
            userResponses,
            pagination
        );
    }

    public ApiResponse<UserResponse> getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return ApiResponse.success(
            HttpStatus.OK.value(),
            "OK",
            "User found",
            mapToUserResponse(user)
        );
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getId().toString())
                .fullname(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .token(null)
                .preferredGenres(user.getPreferredGenres())
                .profilePicture(user.getProfilePicture())
                .build();
    }

    @Transactional
    public void saveUserPreferences(PreferenceRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setPreferredGenres(request.getGenres());
        userRepository.save(user);
    }

    public void deleteByUsername(String username) {
        var user = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        userRepository.delete(user);
    }
    @Transactional
public void updateUserInfo(UUID userId, String name, String password) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

    if (name != null && !name.isBlank()) {
        user.setName(name);
    }

    if (password != null && !password.isBlank()) {
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
    }

    userRepository.save(user);
}


}
