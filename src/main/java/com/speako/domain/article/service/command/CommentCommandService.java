package com.speako.domain.article.service.command;

import com.speako.domain.article.domain.Article;
import com.speako.domain.article.domain.Comment;
import com.speako.domain.article.dto.reqDTO.CommentReqDTO;
import com.speako.domain.article.exception.ArticleErrorCode;
import com.speako.domain.article.repository.ArticleRepository;
import com.speako.domain.article.repository.CommentRepository;
import com.speako.domain.user.domain.User;
import com.speako.domain.user.repository.UserRepository;
import com.speako.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentCommandService {
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public void createComment(Long userId, CommentReqDTO dto){
        Article article = articleRepository.findById(dto.articleId())
                .orElseThrow(() -> new CustomException(ArticleErrorCode.ARTICLE_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ArticleErrorCode.USER_NOT_FOUND));

        Comment comment = Comment.builder()
                .user(user)
                .article(article)
                .content(dto.content())
                .build();

        commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, Long userId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ArticleErrorCode.COMMENT_NOT_FOUND));

        if(!comment.getUser().getId().equals(userId)){
            throw new CustomException(ArticleErrorCode.FORBIDDEN);
        }

        commentRepository.delete(comment);
    }
}
