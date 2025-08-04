package com.speako.domain.article.dto.reqDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ArticleContentReqDTO(

        @JsonProperty("user_badge_id")
        Long userBadgeId,

        String content
) {
}