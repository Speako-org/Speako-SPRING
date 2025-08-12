package com.speako.domain.article.controller;

import com.speako.domain.article.dto.reqDTO.ArticleContentReqDTO;
import com.speako.domain.article.dto.reqDTO.CursorPageRequest;
import com.speako.domain.article.dto.resDTO.CursorPageResDTO;
import com.speako.domain.article.dto.resDTO.GetArticleResDTO;
import com.speako.domain.article.service.command.ArticleCommandService;
import com.speako.domain.article.service.query.ArticleQueryService;
import com.speako.domain.auth.annotation.LoginUser;
import com.speako.domain.security.principal.CustomUserDetails;
import com.speako.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    public CustomResponse<CursorPageResDTO<GetArticleResDTO>> getArticlesList(
            @Parameter(description = "조회 시작 커서 ID, null이면 처음부터 조회")
            @RequestParam(required = false) Long cursorId,
            @Parameter(description = "한 번에 조회할 게시글 개수 (기본값 10)")
            @RequestParam(defaultValue = "10") int size
    ){
        CursorPageRequest pageRequest = new CursorPageRequest(cursorId, size);
        return CustomResponse.onSuccess(articleQueryService.getAllArticles(pageRequest));
    }


    @GetMapping("/list/{articleId}")
    @Operation(method = "GET", summary = "특정 Article 조회 API", description = "특정 Article을 조회하는 API입니다.")
    public CustomResponse<GetArticleResDTO> getArticle(
            @Parameter(description = "조회할 Article ID")
            @PathVariable Long articleId
    ) {
        GetArticleResDTO article = articleQueryService.getArticle(articleId);
        return CustomResponse.onSuccess(article);
    }


    @PostMapping("/post")
    @Operation(method = "POST", summary = "Article 작성 API",
            description = "Article을 작성하는 API입니다.\n\n"
                    + "**예시:**\n"
                    + "- user_badge_id: 1\n"
                    + "- content: 뱃지 받기 성공했어요!")
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
            @Parameter(description = "삭제할 Article ID")
            @PathVariable Long articleId,
            @LoginUser CustomUserDetails customUserDetails
    ){
        Long userId = customUserDetails.getId();
        articleCommandService.deleteArticle(userId, articleId);
        return CustomResponse.onSuccess("글 삭제가 완료되었습니다.");
    }


    @PatchMapping("/update/{articleId}")
    @Operation(method = "PATCH", summary = "Article 수정 API",
            description = "특정 Article에서 변경 사항을 수정해주는 API입니다.(둘 중 하나만 작성해도 됨)\n\n"
                    + "**예시:**\n"
                    + "- user_badge_id: 1\n"
                    + "- content: 글 수정할게요")
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
