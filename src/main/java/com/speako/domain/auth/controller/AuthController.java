package com.speako.domain.auth.controller;

import com.speako.domain.auth.dto.reqDTO.LoginRequest;
import com.speako.domain.auth.dto.reqDTO.SignupRequest;
import com.speako.domain.auth.dto.resDTO.JwtResponse;
import com.speako.domain.auth.dto.resDTO.LoginResponse;
import com.speako.domain.auth.service.AuthService;
import com.speako.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(method = "POST", summary = "회원가입 API",
            description = "사용자로부터 정보를 받아서 회원가입시켜주는 API입니다.\n\n"
                    + "**예시:**\n"
                    + "- email: `user@example.com`\n"
                    + "- password: `securePassword123`\n"
                    + "- gender: `Male`, `Female`, `Other` 중 하나")
    public CustomResponse<Long> signup(@RequestBody SignupRequest request) {

        return CustomResponse.onSuccess(authService.signup(request));
    }

    @PostMapping("/login")
    @Operation(method = "POST", summary = "로그인 API",
            description = "회원가입된 이메일&비밀번호를 통해 로그인하는 API입니다.\n\n"
                    + "**예시:**\n"
                    + "- email: `user@example.com`\n"
                    + "- password: `securePassword123`")
    public CustomResponse<LoginResponse> login(@RequestBody LoginRequest request) {

        return CustomResponse.onSuccess(authService.login(request));
    }

    @PostMapping("/logout")
    @Operation(method = "POST", summary = "로그아웃 API", description = "현재 로그인된 access 토큰을 받아서 로그아웃시켜주는 API입니다.")
    public CustomResponse<String> logout(HttpServletRequest httpServletRequest) {

        authService.logout(httpServletRequest);
        return CustomResponse.onSuccess("성공적으로 로그아웃되었습니다.");
    }

    @PostMapping("/reissue")
    @Operation(method = "POST", summary = "토큰 재발급 API", description = "refresh 토큰을 사용하여 access&refresh 토큰 쌍을 재발급 받는 API입니다.")
    public CustomResponse<JwtResponse> reissue(
            @Parameter(description = "가장 최근에 발급된, 만료되지 않은 refresh 토큰을 넣어주세요.")
            @RequestParam String refreshToken) {

        return CustomResponse.onSuccess(authService.reissue(refreshToken));
    }
}
