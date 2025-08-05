package com.speako.domain.userinfo.dto.reqDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// 한줄소개 update 요청 정보 DTO
public record UpdateSelfCommentReqDTO(

        @NotBlank(message = "자기 소개는 비어 있을 수 없습니다.")
        @Size(max = 50, message = "자기 소개는 최대 50자까지 입력할 수 있습니다.")
        String selfComment
) {
}
