package com.speako.domain.challenge.service.query;

import com.speako.domain.challenge.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BadgeQueryService {

    private final BadgeRepository badgeRepository;

    // 전체 뱃지 종류 수 카운트
    public int countTotalBadges() {
        return (int) badgeRepository.count();
    }
}
