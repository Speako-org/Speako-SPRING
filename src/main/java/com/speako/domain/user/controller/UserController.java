package com.speako.domain.user.controller;

import com.speako.domain.auth.annotation.LoginUser;
import com.speako.domain.security.principal.CustomUserDetails;
import com.speako.domain.user.dto.reqDTO.UpdateUserNameReqDTO;
import com.speako.domain.user.dto.resDTO.UpdateUserNameResDTO;
import com.speako.domain.user.service.command.UserCommandService;
import com.speako.domain.userinfo.dto.reqDTO.UpdateMainUserBadgeReqDTO;
import com.speako.domain.userinfo.dto.resDTO.UpdateMainUserBadgeResDTO;
import com.speako.domain.userinfo.dto.resDTO.userAchievement.UpdateImageTypeResDTO;
import com.speako.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserCommandService userCommandService;

    @PatchMapping("/badge/main")
    @Operation(method = "PATCH", summary = "대표 뱃지 update API", description = "사용자의 대표 타이틀 뱃지를 변경하는 API입니다.")
    public CustomResponse<UpdateMainUserBadgeResDTO> updateRepresentativeBadge(
            @LoginUser CustomUserDetails userDetails,
            @Valid @RequestBody UpdateMainUserBadgeReqDTO updateMainUserBadgeReqDTO) {

        UpdateMainUserBadgeResDTO resDTO = userCommandService.updateMainUserBadge(
                userDetails.getId(),
                updateMainUserBadgeReqDTO.newMainUserBadgeId()
        );
        return CustomResponse.onSuccess(resDTO);
    }

    @PatchMapping("/image")
    @Operation(method = "PATCH", summary = "프로필 이미지 update API", description = "사용자의 대표 이미지를 변경하는 API입니다.")
    public CustomResponse<UpdateImageTypeResDTO> updateProfileImage(
            @LoginUser CustomUserDetails userDetails,
            @RequestParam String newImageName) {

        UpdateImageTypeResDTO resDTO = userCommandService.updateProfileImage(userDetails.getId(), newImageName);
        return CustomResponse.onSuccess(resDTO);
    }

    @PatchMapping("/username")
    @Operation(method = "PATCH", summary = "사용자의 userName update API", description = "현재 사용자의 userName을 변경하는 API입니다.")
    public CustomResponse<UpdateUserNameResDTO> updateUserName(
            @LoginUser CustomUserDetails userDetails,
            @Valid @RequestBody UpdateUserNameReqDTO updateUserNameReqDTO) {

        UpdateUserNameResDTO resDTO = userCommandService.updateUserName(
                userDetails.getId(),
                updateUserNameReqDTO.newUserName()
        );
        return CustomResponse.onSuccess(resDTO);
    }
}
