package com.speako.domain.challenge.controller;


import com.speako.domain.auth.annotation.LoginUser;
import com.speako.domain.challenge.converter.UserBadgeConverter;
import com.speako.domain.challenge.converter.UserChallengeConverter;
import com.speako.domain.challenge.domain.UserBadge;
import com.speako.domain.challenge.domain.UserChallenge;
import com.speako.domain.challenge.dto.UserBadgeResponse;
import com.speako.domain.challenge.dto.UserChallengeResponse;
import com.speako.domain.challenge.service.query.UserChallengeQueryService;
import com.speako.domain.security.principal.CustomUserDetails;
import com.speako.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserChallengeController {

    private final UserChallengeQueryService userChallengeQueryService;

    @GetMapping("/challenges")
    @Operation(summary = "활성 챌린지 조회", description = "사용자의 현재 진행 중인 챌린지를 조회합니다.")
    public CustomResponse<List<UserChallengeResponse>> getActiveChallenges(
            @LoginUser CustomUserDetails customUserDetails
    ) {
        List<UserChallenge> activeChallenges = userChallengeQueryService.getActiveChallenges(customUserDetails.getId());

        List<UserChallengeResponse> response = activeChallenges.stream()
                .map(UserChallengeConverter::toChallengeResponse)
                .toList();

        return CustomResponse.onSuccess(response);
    }

    @GetMapping("/badges")
    @Operation(summary = "획득한 뱃지 조회", description = "사용자가 획득한 모든 뱃지를 조회합니다.")
    public CustomResponse<List<UserBadgeResponse>> getUserBadges(
            @LoginUser CustomUserDetails customUserDetails
    ) {
        List<UserBadge> userBadges = userChallengeQueryService.getUserBadges(customUserDetails.getId());
        List<UserBadgeResponse> response = userBadges.stream()
                .map(UserBadgeConverter::toUserBadgeResponse)
                .toList();

        return CustomResponse.onSuccess(response);
    }

}
