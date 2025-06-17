package com.PBO.TaleSwipe.dto;

import java.util.List;

import lombok.Data;

@Data
public class PreferenceRequest {
    private String username;
    private List<String> genres;
}
