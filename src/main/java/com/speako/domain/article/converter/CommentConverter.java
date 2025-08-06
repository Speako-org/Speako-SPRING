package com.speako.domain.article.converter;

import com.speako.domain.article.domain.Comment;
import com.speako.domain.article.dto.resDTO.CommentResDTO;
import com.speako.domain.challenge.domain.Badge;

public class CommentConverter {

    public static CommentResDTO toDTO(Comment comment, Badge mainBadge) {
        return new CommentResDTO(
                comment.getId(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getUser().getImageType().getImageUrl(),

                mainBadge != null ? mainBadge.getId() : null,
                mainBadge != null ? mainBadge.getName() : null,

                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
