package com.speako.domain.user.dto.reqDTO;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserNameReqDTO(

        @NotBlank(message = "변경될 UserName은 공백일 수 없습니다.")
        String newUserName
) {
}
