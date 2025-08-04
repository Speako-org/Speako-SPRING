package com.speako.domain.article.service.query;
// query - 조회 서비스 (get, find, search)


import com.speako.domain.article.domain.Article;
import com.speako.domain.article.dto.resDTO.GetArticleResDTO;
import com.speako.domain.article.exception.ArticleErrorCode;
import com.speako.domain.article.repository.ArticleRepository;
import com.speako.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.speako.domain.article.converter.ArticleConverter.toGetArticleResDTO;
import static com.speako.domain.article.converter.ArticleConverter.toGetArticleResDTOList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleQueryService {
    private final ArticleRepository articleRepository;

    public List<GetArticleResDTO> getAllArticles(){
        List<Article> articles = articleRepository.findAllByOrderByCreatedAtDesc();
        return toGetArticleResDTOList(articles);
    }

    public GetArticleResDTO getArticle(Long articleId){
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ArticleErrorCode.ARTICLE_NOT_FOUND));
        return toGetArticleResDTO(article);
    }
}
