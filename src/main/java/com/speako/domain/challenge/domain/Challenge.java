package com.speako.domain.challenge.domain;

import com.speako.domain.challenge.domain.enums.IconCode;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "challenges")
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 200)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "icon_code", nullable = false, updatable = false)
    private IconCode iconCode;

    @Column(nullable = false)
    private Integer level;

    @Column(name = "level_name", nullable = false, length = 20)
    private String levelName;

    @Column(name = "required_amount", nullable = false)
    private Integer requiredAmount;

    @Column(name = "sequence", nullable = false)
    private Integer sequence;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
