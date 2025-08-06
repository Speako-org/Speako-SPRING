package com.speako.domain.article.dto.reqDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CommentReqDTO(

        @JsonProperty("article_id")
        Long articleId,

        String content
) {
}
