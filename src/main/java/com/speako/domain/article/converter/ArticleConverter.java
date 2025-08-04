package com.speako.domain.article.converter;

import com.speako.domain.article.domain.Article;
import com.speako.domain.article.dto.resDTO.GetArticleResDTO;
import com.speako.domain.challenge.domain.UserBadge;
import com.speako.domain.user.domain.User;

import java.util.List;

public class ArticleConverter {
    public static GetArticleResDTO toGetArticleResDTO(Article article) {
        User user =  article.getUser();

        UserBadge userBadge = article.getUserBadge();
        Badge badge = userBadge.getBadge();

        UserBadge mainUserBadge = user.getUserBadges().stream()
                .filter(UserBadge::isMain)
                .findFirst()
                .orElse(null);

        return new GetArticleResDTO(
                user.getId(),
                article.getId(),
                user.getUsername(),
                user.getImageUrl(),
                article.getCreatedAt(),

                mainUserBadge != null ? mainUserBadge.getId() : null,
                mainUserBadge != null ? mainUserBadge.getName() : null,

                userBadge.getId(),
                badge.getName(),
                badge.getDescription(),

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
