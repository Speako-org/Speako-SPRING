package com.speako.domain.userinfo.controller;

import com.speako.domain.auth.annotation.LoginUser;
import com.speako.domain.security.principal.CustomUserDetails;
import com.speako.domain.userinfo.dto.reqDTO.UpdateSelfCommentReqDTO;
import com.speako.domain.userinfo.dto.resDTO.MyPageResDTO;
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
@RequestMapping("/api/users")
public class UserInfoController {

    private final UserInfoCommandService userInfoCommandService;
    private final UserInfoQueryService userInfoQueryService;

    @GetMapping("/{userId}/mypage")
    @Operation(method = "GET", summary = "마이페이지 정보 조회 API",
            description = "본인 또는 타인의 마이페이지 정보를 조회하는 API입니다.\n\n"
                    + "**세부 설명:**\n"
                    + "- `PathVariable 'userId'(targetUserId)`: 조회하려는 마이페이지의 소유자 id\n\n"
                    + "- `본인의 마이페이지를 조회할 경우`(targetUserId가 로그인된 사용자의 userId와 같을 경우)\n\n"
                    + "    - [userId / 유저 대표정보 / 월별통계]만 반환하며, 획득 뱃지리스트는 제외(null)됩니다.\n\n"
                    + "- `타인의 마이페이지를 조회할 경우`(targetUserId가 로그인된 사용자의 userId와 다를 경우)\n\n"
                    + "    - [userId / 유저 대표정보 / 획득 뱃지 리스트]만 반환하며, 월별통계는 제외(null)됩니다.")
    public CustomResponse<MyPageResDTO> getMyPage(
            @LoginUser CustomUserDetails userDetails,
            @PathVariable(value = "userId") Long targetUserId) {

        MyPageResDTO myPageResDTO = userInfoQueryService.getMyPage(userDetails.getId(), targetUserId);
        return CustomResponse.onSuccess(myPageResDTO);
    }

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
