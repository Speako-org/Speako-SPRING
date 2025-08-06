package com.speako.domain.article.controller;

import com.speako.domain.article.service.command.ArticleLikeCommandService;
import com.speako.domain.auth.annotation.LoginUser;
import com.speako.domain.security.principal.CustomUserDetails;
import com.speako.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/articles/like")
public class ArticleLikeController {

    private final ArticleLikeCommandService articleLikeCommandService;

    @PostMapping("/post/{articleId}")
    @Operation(summary = "게시글 좋아요 추가 API", description = "해당 게시글에 좋아요를 추가하는 API입니다.")
    public CustomResponse<String> like(
            @Parameter(description = "좋아요를 누를 게시글 ID", required = true)
            @PathVariable Long articleId,
            @LoginUser CustomUserDetails customUserDetails
    ) {
        Long userId = customUserDetails.getId();
        articleLikeCommandService.likeArticle(articleId, userId);
        return CustomResponse.onSuccess("좋아요를 눌렀습니다.");
    }

    @DeleteMapping("/delete/{articleId}")
    @Operation(summary = "게시글 좋아요 삭제 API", description = "해당 게시글에 누른 좋아요를 삭제하는 API입니다.")
    public CustomResponse<String> unlikeArticle(
            @Parameter(description = "좋아요를 삭제할 게시글 ID", required = true)
            @PathVariable Long articleId,
            @LoginUser CustomUserDetails customUserDetails
    ){
        Long userId = customUserDetails.getId();
        articleLikeCommandService.unlikeArticle(articleId, userId);
        return  CustomResponse.onSuccess("좋아요를 삭제했습니다.");
    }
}
