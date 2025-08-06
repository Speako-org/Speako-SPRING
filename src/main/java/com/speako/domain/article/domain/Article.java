package com.speako.domain.article.domain;

import com.speako.domain.challenge.domain.UserBadge;
import com.speako.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String content;

    @Column(name = "liked_num", nullable = false)
    private int likedNum;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_badge_id", nullable = false)
    private UserBadge userBadge;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void updateBadge(UserBadge userBadge) {
        this.userBadge = userBadge;
    }

    public void updateContent(String content){
        this.content = content;
    }

    public void increaseLikedNum() {
        this.likedNum++;
    }

    public void decreaseLikedNum() {
        if(this.likedNum > 0) {
            this.likedNum--;
        }
    }
}
