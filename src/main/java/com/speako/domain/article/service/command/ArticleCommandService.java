package com.speako.domain.article.service.command;
// command - 명령서비스 (create, update, delete)

import com.speako.domain.article.domain.Article;
import com.speako.domain.article.dto.reqDTO.ArticleContentReqDTO;
import com.speako.domain.article.exception.ArticleErrorCode;
import com.speako.domain.article.repository.ArticleRepository;
import com.speako.domain.challenge.domain.UserBadge;
import com.speako.domain.challenge.repository.UserBadgeRepository;
import com.speako.domain.user.domain.User;
import com.speako.domain.user.repository.UserRepository;
import com.speako.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ArticleCommandService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final UserBadgeRepository userBadgeRepository;

    public void createArticle(Long userId, ArticleContentReqDTO dto){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ArticleErrorCode.USER_NOT_FOUND));

        UserBadge userBadge = userBadgeRepository.findById(dto.userBadgeId())
                .orElseThrow(() -> new CustomException(ArticleErrorCode.BAD_REQUEST));

        Article article = Article.builder()
                .user(user)
                .userBadge(userBadge)
                .content(dto.content())
                .likedNum(0)
                .build();

        articleRepository.save(article);
    }

    public void deleteArticle(Long userId, Long articleId){
    Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ArticleErrorCode.ARTICLE_NOT_FOUND));

        if (!article.getUser().getId().equals(userId)) {
            throw new CustomException(ArticleErrorCode.FORBIDDEN);
        }
        articleRepository.delete(article);
    }

    public void updateArticle(Long userId, Long articleId, ArticleContentReqDTO dto){
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ArticleErrorCode.ARTICLE_NOT_FOUND));
        if (!article.getUser().getId().equals(userId)) {
            throw new CustomException(ArticleErrorCode.FORBIDDEN);
        }

        UserBadge userBadge = userBadgeRepository.findById(dto.userBadgeId())
                .orElseThrow(() -> new CustomException(ArticleErrorCode.BAD_REQUEST));

        article.updateArticle(dto.content(), userBadge);
    }
}
