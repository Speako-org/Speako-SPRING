package com.speako.domain.record.service.command;

import com.speako.domain.record.entity.Record;
import com.speako.domain.record.entity.enums.RecordStatus;
import com.speako.domain.record.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RecordCommandService {

    private final RecordRepository recordRepository;

    public void completeUpload(String s3Path) {

//        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(UserHandler())); -> 이후에는 이런 식 말고 AuthUser 객체를 받아오는 방법도 고려할 것
        Record record = Record.builder()
//                .user(user)
                .s3Path(s3Path)
                .recordStatus(RecordStatus.SAVED)
                .build();

        recordRepository.save(record);
    }
}
