package com.speako.domain.article.converter;

import com.speako.domain.article.domain.Article;
import com.speako.domain.article.dto.resDTO.GetArticleResDTO;
import com.speako.domain.challenge.domain.UserBadge;
import com.speako.domain.user.domain.User;

import java.util.List;

public class ArticleConverter {
    public static GetArticleResDTO toGetArticleResDTO(Article article) {
        User user =  article.getUser();
        UserBadge articleBadge = article.getUserBadge();
        UserBadge mainBadge = user.getUserBadge();

        return new GetArticleResDTO(
                user.getId(),
                article.getId(),
                user.getUsername(),
                user.getImageUrl(),
                article.getCreatedAt(),
                mainBadge != null ? mainBadge.getTitle() : null,
                articleBadge != null ? articleBadge.getId() : null,
                articleBadge != null ? articleBadge.getTitle() : null,
                articleBadge != null ? articleBadge.getDescription() : null,
                article.getContent(),
                article.getLikedNum()
                //article.getcommentNum()
        );
    }

    public static List<GetArticleResDTO> toGetArticleResDTOList(List<Article> articles) {
        return articles.stream()
                .map(ArticleConverter::toGetArticleResDTO)
                .toList();
    }
}
