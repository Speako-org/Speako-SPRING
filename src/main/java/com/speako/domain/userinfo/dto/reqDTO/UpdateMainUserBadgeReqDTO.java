package com.speako.domain.userinfo.dto.reqDTO;

// 대표 뱃지 update 요청 정보 DTO
public record UpdateMainUserBadgeReqDTO(

        Long currentMainUserBadgeId,
        Long newMainUserBadgeId
) {
}
