package com.PBO.TaleSwipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponse {
    private String username;
    private String name;         // Nama lengkap user
    private String profilePicture; // URL foto profil (jika ada)
}
