package com.speako.domain.article.service.query;

import com.speako.domain.article.converter.ArticleConverter;
import com.speako.domain.article.domain.Article;
import com.speako.domain.article.dto.reqDTO.CursorPageRequest;
import com.speako.domain.article.dto.resDTO.GetArticleResDTO;
import com.speako.domain.article.exception.ArticleErrorCode;
import com.speako.domain.article.repository.ArticleRepository;
import com.speako.domain.article.repository.CommentRepository;
import com.speako.domain.challenge.domain.Badge;
import com.speako.domain.challenge.domain.UserBadge;
import com.speako.domain.challenge.repository.UserBadgeRepository;
import com.speako.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.speako.domain.article.converter.ArticleConverter.toGetArticleResDTO;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleQueryService {
    private final ArticleRepository articleRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final CommentRepository commentRepository;

    public List<GetArticleResDTO> getAllArticles(CursorPageRequest pageRequest){

        Long lastArticleId = pageRequest.cursorId();
        int size = pageRequest.size();

        List<Article> articles;

        if(lastArticleId == null){
            articles = articleRepository.findTopNOrderByIdDesc(PageRequest.of(0, size));
        } else {
            articles = articleRepository.findByIdLessThanOrderByIdDesc(lastArticleId, PageRequest.of(0, size));
        }

        List<Long> articleIds = articles.stream()
                .map(Article::getId)
                .toList();

        Map<Long, Integer> commentNumMap = commentRepository.countByArticleIdIn(articleIds).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Long) row[1]).intValue()
                ));

        Function<Long, Badge> mainBadgeProvider = userId ->
                userBadgeRepository.findByUserIdAndIsMain(userId)
                        .map(UserBadge::getBadge)
                        .orElse(null);

        return ArticleConverter.toGetArticleResDTOList(articles, mainBadgeProvider, commentNumMap);
    }

    public GetArticleResDTO getArticle(Long articleId){
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ArticleErrorCode.ARTICLE_NOT_FOUND));

        Long authorId = article.getUser().getId();

        Badge mainBadge = userBadgeRepository.findByUserIdAndIsMain(authorId)
                .map(UserBadge::getBadge)
                .orElse(null);

        int commentCount = commentRepository.countByArticleId(articleId);

        return toGetArticleResDTO(article, mainBadge, commentCount);
    }
}
