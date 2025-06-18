package com.PBO.TaleSwipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LikeBookmarkCountResponse {
    private int likeCount;
    private int bookmarkCount;
}
