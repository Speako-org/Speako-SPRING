package com.speako.domain.article.controller;

import com.speako.domain.article.dto.reqDTO.CommentReqDTO;
import com.speako.domain.article.dto.reqDTO.CursorPageRequest;
import com.speako.domain.article.dto.resDTO.CommentResDTO;
import com.speako.domain.article.dto.resDTO.CursorPageResDTO;
import com.speako.domain.article.service.command.CommentCommandService;
import com.speako.domain.article.service.query.CommentQueryService;
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
@RequestMapping("api/articles/comment")
public class CommentController {
    private final CommentCommandService commentCommandService;
    private final CommentQueryService commentQueryService;

    @PostMapping
    @Operation(summary = "댓글 작성 API", description = "특정 게시글에 댓글을 작성하는 API입니다.")
    public CustomResponse<String> createComment(
            @LoginUser CustomUserDetails userDetails,
            @Parameter(description = "작성할 댓글 데이터", required = true)
            @RequestBody CommentReqDTO dto
    ){
        commentCommandService.createComment(userDetails.getId(), dto);
        return CustomResponse.onSuccess("댓글 작성 완료");
    }

    @GetMapping("/{articleId}")
    @Operation(summary = "댓글 목록 조회 API", description = "특정 게시글의 댓글 목록을 커서 기반으로 조회하는 API입니다.")
    public CustomResponse<CursorPageResDTO<CommentResDTO>> getComments(
            @Parameter(description = "댓글 목록을 조회할 게시글 ID", required = true)
            @PathVariable Long articleId,
            @Parameter(description = "커서 ID, null이면 처음부터 조회")
            @RequestParam(required = false) Long cursorId,
            @Parameter(description = "한 번에 조회할 댓글 개수 (기본값 10)")
            @RequestParam(defaultValue = "10") int size
    ){
        CursorPageRequest pageRequest = new CursorPageRequest(cursorId, size);
        return CustomResponse.onSuccess(commentQueryService.getCommentsByArticleId(articleId, pageRequest));
    }

    @DeleteMapping("/delete/{commentId}")
    @Operation(summary = "댓글 삭제 API", description = "특정 댓글을 삭제하는 API입니다.")
    public CustomResponse<String> deleteComment(
            @Parameter(description = "삭제할 댓글 ID", required = true)
            @PathVariable Long commentId,
            @LoginUser CustomUserDetails userDetails
    ){
        commentCommandService.deleteComment(commentId, userDetails.getId());
        return CustomResponse.onSuccess("댓글 삭제 완료");
    }
}
