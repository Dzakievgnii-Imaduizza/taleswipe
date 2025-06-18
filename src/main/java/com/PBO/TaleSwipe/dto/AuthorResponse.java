package com.PBO.TaleSwipe.dto;

import java.util.UUID;

import com.PBO.TaleSwipe.model.User;
 
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorResponse {
    private UUID id;
    private String username;
    private String name;
    private String profilePicture;           // Optional, isi "" jika belum ada
    private int followerCount;    // Optional
    private boolean followedByCurrentUser; // Optional
    public static AuthorResponse fromEntity(User author) {
    return AuthorResponse.builder()
        .username(author.getUsername())
        .name(author.getName())
        .profilePicture(author.getProfilePicture() == null ? "" : author.getProfilePicture())
        .followerCount(author.getFollowers() != null ? author.getFollowers().size() : 0)
        .followedByCurrentUser(false) // atau isi sesuai kebutuhan Anda
        .build();
}

}
