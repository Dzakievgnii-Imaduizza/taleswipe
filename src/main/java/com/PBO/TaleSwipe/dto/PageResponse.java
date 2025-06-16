package com.PBO.TaleSwipe.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageResponse {
    private String pageId;
    private int pageNumber;
    private String content;
}
