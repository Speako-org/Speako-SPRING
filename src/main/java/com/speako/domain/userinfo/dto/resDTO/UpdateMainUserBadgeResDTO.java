package com.speako.domain.userinfo.dto.resDTO;

// 대표 뱃지 update된 정보 DTO
public record UpdateMainUserBadgeResDTO(

        Long userId,
        Long updatedMainUserBadgeId,
        String updatedMainUserBadgeName
) {
}
