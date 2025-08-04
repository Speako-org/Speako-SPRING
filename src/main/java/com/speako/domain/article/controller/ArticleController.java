package com.speako.domain.article.controller;

import com.speako.domain.article.dto.reqDTO.ArticleContentReqDTO;
import com.speako.domain.article.dto.resDTO.GetArticleResDTO;
import com.speako.domain.article.service.command.ArticleCommandService;
import com.speako.domain.article.service.query.ArticleQueryService;
import com.speako.domain.auth.annotation.LoginUser;
import com.speako.domain.security.principal.CustomUserDetails;
import com.speako.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleQueryService articleQueryService;
    private final ArticleCommandService articleCommandService;


    @GetMapping("/list")
    @Operation(method = "GET", summary = "Article 전체 조회 API", description = "모든 Article을 조회하는 API입니다.")
    public CustomResponse<List<GetArticleResDTO>> getArticlesList(){
        List<GetArticleResDTO> articles = articleQueryService.getAllArticles();
        return CustomResponse.onSuccess(articles);
    }


    @GetMapping("/list/{articleId}")
    @Operation(method = "GET", summary = "특정 Article 조회 API", description = "특정 Article을 조회하는 API입니다.")
    public CustomResponse<GetArticleResDTO> getArticle(@PathVariable Long articleId) {
        GetArticleResDTO article = articleQueryService.getArticle(articleId);
        return CustomResponse.onSuccess(article);
    }


    @PostMapping("/post")
    @Operation(method = "POST", summary = "Article 작성 API", description = "Article을 작성하는 API입니다.")
    public CustomResponse<String> postArticle(
            @RequestBody ArticleContentReqDTO requestDto,
            @LoginUser CustomUserDetails customUserDetails
    ) {
        Long userId = customUserDetails.getId();
        articleCommandService.createArticle(userId, requestDto);
        return CustomResponse.onSuccess("글 작성이 완료되었습니다.");
    }


    @DeleteMapping("/delete/{articleId}")
    @Operation(method = "DELETE", summary = "Article 삭제 API", description = "특정 Article을 삭제하는 API입니다.")
    public CustomResponse<String> deleteArticle(
            @PathVariable Long articleId,
            @LoginUser CustomUserDetails customUserDetails
    ){
        Long userId = customUserDetails.getId();
        articleCommandService.deleteArticle(userId, articleId);
        return CustomResponse.onSuccess("글 삭제가 완료되었습니다.");
    }


    @PatchMapping("/update/{articleId}")
    @Operation(method = "PATCH", summary = "Article 수정 API", description = "특정 Article에서 변경 사항을 수정해주는 API입니다.")
    public CustomResponse<String> updateArticle(
            @PathVariable Long articleId,
            @RequestBody ArticleContentReqDTO requestDto,
            @LoginUser CustomUserDetails customUserDetails
    ) {
        Long userId = customUserDetails.getId();
        articleCommandService.updateArticle(userId, articleId, requestDto);
        return CustomResponse.onSuccess("글 수정이 완료되었습니다.");
    }

}
