package com.speako.domain.article.dto.resDTO;

import java.util.List;

public record CursorPageResDTO<T>(
    List<T> content,
    Long nextCursorId,
    boolean hasNext
) {}
