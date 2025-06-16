package com.PBO.TaleSwipe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResponse {
    private int currentPage;
    private int totalPage;
    private int limit;
    private long totalItem;
} 
