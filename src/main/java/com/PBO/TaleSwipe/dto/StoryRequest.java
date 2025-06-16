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
public class StoryRequest {
    private String title;
    private String description;  // Menambahkan deskripsi cerita
    private List<String> genres;
    private List<String> pageContents; 
}
