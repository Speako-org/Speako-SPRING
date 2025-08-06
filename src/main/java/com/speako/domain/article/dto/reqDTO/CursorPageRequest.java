package com.speako.domain.article.dto.reqDTO;

public record CursorPageRequest(
        Long cursorId,
        int size
) {
}
