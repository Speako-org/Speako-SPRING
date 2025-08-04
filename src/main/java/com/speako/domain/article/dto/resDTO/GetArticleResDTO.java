package com.speako.domain.article.dto.resDTO;

import java.time.LocalDateTime;

public record GetArticleResDTO(
        Long userId,
        Long articleId,
        String username,
        String ImageUrl,
        LocalDateTime createdAt,
        String mainBadgeTitle,
        Long userBadgeId,
        String badgeTitle,
        String badgeDescription,
        String content,
        int likedNum
        //int commentNum
) {
}
