package com.speako.domain.article.service.query;
// query - 조회 서비스 (get, find, search)


import com.speako.domain.article.converter.ArticleConverter;
import com.speako.domain.article.domain.Article;
import com.speako.domain.article.dto.resDTO.GetArticleResDTO;
import com.speako.domain.article.exception.ArticleErrorCode;
import com.speako.domain.article.repository.ArticleRepository;
import com.speako.domain.article.repository.CommentRepository;
import com.speako.domain.challenge.domain.UserBadge;
import com.speako.domain.challenge.repository.UserBadgeRepository;
import com.speako.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.speako.domain.article.converter.ArticleConverter.toGetArticleResDTO;
import static com.speako.domain.article.converter.ArticleConverter.toGetArticleResDTOList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleQueryService {
    private final ArticleRepository articleRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final CommentRepository commentRepository;

    public List<GetArticleResDTO> getAllArticles(){
        List<Article> articles = articleRepository.findAllByOrderByCreatedAtDesc();
        List<Long> articleIds = articles.stream()
                .map(Article::getId)
                .toList();

        Map<Long, Long> commentNumMap = commentRepository.countByArticleIdIn(articleIds).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().intValue()));

        Function<Long, UserBadge> mainBadgeProvider = userId ->
                userBadgeRepository.findByUserIdAndIsMain(userId).orElse(null);

        return ArticleConverter.toGetArticleResDTOList(articles, mainBadgeProvider, commentNumMap);
    }

    public GetArticleResDTO getArticle(Long articleId){
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ArticleErrorCode.ARTICLE_NOT_FOUND));

        Long authorId = article.getUser().getId();

        UserBadge mainBadge = userBadgeRepository.findByUserIdAndIsMain(authorId)
                .map(UserBadge::getBadge)
                .orElse(null);

        int commentCount = commentRepository.countByArticleId(articleId);

        return toGetArticleResDTO(article, mainBadge, commentCount);
    }
}
