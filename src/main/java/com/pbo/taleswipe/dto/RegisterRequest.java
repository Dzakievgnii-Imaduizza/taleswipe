package com.PBO.TaleSwipe.dto;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String username;
    private String name;
    private String email;
    private String password;
    private String tanggalLahir;
    private List<String> preferensiGenre;
} 
