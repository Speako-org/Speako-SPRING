package com.speako.domain.userinfo.dto.resDTO.userAchievement;

// 한줄소개 update된 정보 DTO
public record UpdateSelfCommentResDTO(

        Long userId,
        String updatedSelfComment
) {}
