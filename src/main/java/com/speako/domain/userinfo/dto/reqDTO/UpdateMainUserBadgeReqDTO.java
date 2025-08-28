package com.speako.domain.userinfo.dto.reqDTO;

import jakarta.validation.constraints.NotNull;

// 대표 뱃지 update 요청 정보 DTO
public record UpdateMainUserBadgeReqDTO(

        @NotNull(message = "변경할 대표 뱃지의 id는 필수입니다.")
        Long newMainUserBadgeId
) {
}
