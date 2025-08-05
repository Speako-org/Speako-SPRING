package com.speako.domain.userinfo.dto.resDTO.userAchievement;

// 프로필 이미지 업데이트 응답 DTO
public record UpdateImageTypeResDTO(

        Long userId,
        String newImageTypeUrl
) {}
