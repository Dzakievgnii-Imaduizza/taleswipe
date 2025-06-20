package com.PBO.TaleSwipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PageResponse {
    private String pageId;
    private int pageNumber;
    private String content;
}
