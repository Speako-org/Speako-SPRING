package com.speako.domain.article.service.command;

import com.speako.domain.article.domain.Article;
import com.speako.domain.article.domain.Like;
import com.speako.domain.article.exception.ArticleErrorCode;
import com.speako.domain.article.repository.ArticleRepository;
import com.speako.domain.article.repository.LikeRepository;
import com.speako.domain.article.service.query.ArticleQueryService;
import com.speako.domain.user.domain.User;
import com.speako.domain.user.repository.UserRepository;
import com.speako.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ArticleLikeCommandService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    public void likeArticle(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ArticleErrorCode.ARTICLE_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ArticleErrorCode.USER_NOT_FOUND));

        if(likeRepository.existsByUserIdAndArticleId(userId, articleId)) {
            throw new CustomException(ArticleErrorCode.ALREADY_LIKED);
        }

        article.increaseLikedNum();

        Like like = Like.builder()
                .user(user)
                .article(article)
                .build();

        likeRepository.save(like);
    }

    public void unlikeArticle(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ArticleErrorCode.ARTICLE_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ArticleErrorCode.USER_NOT_FOUND));

        if(!likeRepository.existsByUserIdAndArticleId(userId, articleId)) {
            throw new CustomException(ArticleErrorCode.LIKE_NOT_FOUND);
        }
        article.decreaseLikedNum();
        likeRepository.deleteByUserIdAndArticleId(userId, articleId);
    }
}
