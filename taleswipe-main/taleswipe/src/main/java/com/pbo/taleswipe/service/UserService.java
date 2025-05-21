package com.pbo.taleswipe.service;

import com.pbo.taleswipe.dto.LoginRequest;
import com.pbo.taleswipe.dto.RegisterRequest;
import com.pbo.taleswipe.dto.response.ApiResponse;
import com.pbo.taleswipe.dto.response.ErrorResponse;
import com.pbo.taleswipe.dto.response.PaginationResponse;
import com.pbo.taleswipe.dto.response.UserResponse;
import com.pbo.taleswipe.model.User;
import com.pbo.taleswipe.model.UserRole;
import com.pbo.taleswipe.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public ApiResponse<UserResponse> register(RegisterRequest request) {
        // Check if username or email already exists
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

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .tanggalLahir(request.getTanggalLahir())
                .preferensiGenre(request.getPreferensiGenre())
                .role(UserRole.USER)
                .build();

        user = userRepository.save(user);
        String token = jwtService.generateToken(user);

        UserResponse response = mapToUserResponse(user);
        return ApiResponse.success(
            HttpStatus.CREATED.value(),
            "CREATED",
            "User created",
            response
        );
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
            UserResponse response = mapToUserResponse(user);

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
                .build();
    }
} 