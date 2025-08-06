package com.speako.domain.article.dto.resDTO;

import java.time.LocalDateTime;

public record CommentResDTO(
        Long commentId,
        Long userId,
        String username,
        String ImageType,
        Long mainBadgeId,
        String mainBadgeTitle,
        String content,
        LocalDateTime createdAt
) {
}
