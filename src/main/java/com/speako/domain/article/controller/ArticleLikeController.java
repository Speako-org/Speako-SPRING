package com.speako.domain.article.controller;

import com.speako.domain.article.service.command.ArticleLikeCommandService;
import com.speako.domain.auth.annotation.LoginUser;
import com.speako.domain.security.principal.CustomUserDetails;
import com.speako.global.apiPayload.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/articles/like")
public class ArticleLikeController {

    private final ArticleLikeCommandService articleLikeCommandService;

    @PostMapping("/post/{articleId}")
    public CustomResponse<String> like(
            @PathVariable Long articleId,
            @LoginUser CustomUserDetails customUserDetails
    ) {
        Long userId = customUserDetails.getId();
        articleLikeCommandService.likeArticle(articleId, userId);
        return CustomResponse.onSuccess("좋아요를 눌렀습니다.");
    }

    @DeleteMapping("/delete/{articleId}")
    public CustomResponse<String> unlikeArticle(
            @PathVariable Long articleId,
            @LoginUser CustomUserDetails customUserDetails
    ){
        Long userId = customUserDetails.getId();
        articleLikeCommandService.unlikeArticle(articleId, userId);
        return  CustomResponse.onSuccess("좋아요를 삭제했습니다.");
    }
}
