package com.PBO.TaleSwipe.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor; // ✅ import ini

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String profilePicture;
    private String userId;
    private String fullname;
    private String email;
    private String role;
    private String token;
    private List<String> preferredGenres; // ✅ tambahkan ini
}
