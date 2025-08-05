package com.speako.domain.userinfo.controller;

import com.speako.domain.auth.annotation.LoginUser;
import com.speako.domain.security.principal.CustomUserDetails;
import com.speako.domain.userinfo.dto.reqDTO.UpdateSelfCommentReqDTO;
import com.speako.domain.userinfo.dto.resDTO.UserInfoResDTO;
import com.speako.domain.userinfo.dto.resDTO.userAchievement.UpdateSelfCommentResDTO;
import com.speako.domain.userinfo.service.command.UserInfoCommandService;
import com.speako.domain.userinfo.service.query.UserInfoQueryService;
import com.speako.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users-info")
public class UserInfoController {

    private final UserInfoCommandService userInfoCommandService;
    private final UserInfoQueryService userInfoQueryService;

    @GetMapping("/mypage")
    @Operation(method = "GET", summary = "사용자의 마이페이지 전체 정보 조회 API", description = "현재 유저의 마이페이지 정보(사용자 전체 통계+월별통계)를 조회하는 API입니다.")
    public CustomResponse<UserInfoResDTO> getUserInfo(
            @LoginUser CustomUserDetails userDetails) {

        UserInfoResDTO userInfoResDTO = userInfoQueryService.getUserInfo(userDetails.getId());
        return CustomResponse.onSuccess(userInfoResDTO);
    }

    /* 유저 대표정보만 get하는 API 구현 필요
        - 타인의 마이페이지 조회 시 사용 예정 (userBadge 조회 API랑 각각 실행되거나, userBadge도 같이 조회하거나 둘 중 하나)
        - @LoginUser와 userId 둘 다 받아와서, 비교해서 같으면 진짜 내 페이지, 다르면 상대페이지 조회되도록 구현하는 방법도 존재
     */

    @PatchMapping("/self-comment")
    @Operation(method = "PATCH", summary = "한줄소개 update API", description = "사용자의 한줄소개를 변경하는 API입니다.")
    public CustomResponse<UpdateSelfCommentResDTO> updateSelfComment(
            @LoginUser CustomUserDetails userDetails,
            @Valid @RequestBody UpdateSelfCommentReqDTO updateSelfCommentReqDTO) {

        UpdateSelfCommentResDTO resDTO = userInfoCommandService.updateSelfComment(
                userDetails.getId(),
                updateSelfCommentReqDTO
        );
        return CustomResponse.onSuccess(resDTO);
    }
}
