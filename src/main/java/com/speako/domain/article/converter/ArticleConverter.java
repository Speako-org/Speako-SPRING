package com.speako.domain.article.converter;

import com.speako.domain.article.domain.Article;
import com.speako.domain.article.dto.resDTO.GetArticleResDTO;
import com.speako.domain.challenge.domain.UserBadge;
import com.speako.domain.user.domain.User;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ArticleConverter {
    public static GetArticleResDTO toGetArticleResDTO(Article article, UserBadge mainBadge,  int commentNum) {
        User user =  article.getUser();

        UserBadge userBadge = article.getUserBadge();
        Badge badge = userBadge.getBadge();

        return new GetArticleResDTO(
                user.getId(),
                article.getId(),
                user.getUsername(),
                user.getImageUrl(),
                article.getCreatedAt(),

                mainBadge != null ? mainBadge.getId() : null,
                mainBadge != null ? mainBadge.getName() : null,

                userBadge.getId(),
                badge.getName(),
                badge.getDescription(),

                article.getContent(),
                article.getLikedNum(),
                commentNum
        );
    }

    public static List<GetArticleResDTO> toGetArticleResDTOList(
            List<Article> articles,
            Function<Long, UserBadge> mainBadgeProvider,
            Map<Long, Integer> commentNumMap
    ) {
        return articles.stream()
                .map(article -> {
                    Long userId = article.getUser().getId();
                    UserBadge mainBadge = mainBadgeProvider.apply(userId);
                    int commentNum = commentNumMap.getOrDefault(article.getId(),0);
                    return toGetArticleResDTO(article, mainBadge, commentNum);
                })
                .toList();
    }
}
