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
    private String username;
    private String role;
    private String token;
    private List<String> preferredGenres; // tambah ini jika ingin field bio
    private int followerCount; // tambah ini
    private boolean followedByCurrentUser; // tambah ini
    private int followingCount;
    private int totalLikes;
}
