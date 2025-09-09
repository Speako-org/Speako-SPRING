package com.speako.domain.user.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.concurrent.ThreadLocalRandom;

public enum ImageType {

    // 노란 두건, 수염, 커피+전화
    BEARD_MAN_COFFEE_CALL("1", "https://speako-bucket.s3.ap-northeast-2.amazonaws.com/profile-image/ad0230e51cf72c468f60f22a06ee0b26b40e974f.png"),
    // 베이지 히잡, 파란 가방 여성
    HIJAB_WOMAN_BAG("3", "https://speako-bucket.s3.ap-northeast-2.amazonaws.com/profile-image/3ef290bc29275437b13e4e702a43b9c3e4b5bafe.png"),
    // 백인+흑인 남성 다정한 모습
    FRIENDLY_DUO("5", "https://speako-bucket.s3.ap-northeast-2.amazonaws.com/profile-image/4f5f4fc24d4fb323844cbdd0f8c3864a98b89c8b.png"),
    // 파란머리, 청각보조장치, 빨간 옷 소녀
    BLUE_HAIR_HEARING_AID("12", "https://speako-bucket.s3.ap-northeast-2.amazonaws.com/profile-image/eaa320717b7e77fd08d1bdaf9802cc375eb36366.png"),
    // 백반증, 금귀걸이 폭탄머리 여성
    VITILIGO_AFRO_WOMAN("15", "https://speako-bucket.s3.ap-northeast-2.amazonaws.com/profile-image/b45fff6b8e9ca09258e544c7bd3e6cd00180d427.png"),
    // 흰 마스크, 연두색 가운 간호사
    NURSE_MASK_GREEN("17", "https://speako-bucket.s3.ap-northeast-2.amazonaws.com/profile-image/a7e8150b48421155ec56805f777056f57141df11.png"),
    // 여드름 난 흑인, 초록색 VR
    ACNE_VR_BOY("21", "https://speako-bucket.s3.ap-northeast-2.amazonaws.com/profile-image/870010419e5f5601c0d7438d4c0e20fa10cc740f.png"),
    // 책 속 애벌레
    BOOKWORM_CATERPILLAR("22", "https://speako-bucket.s3.ap-northeast-2.amazonaws.com/profile-image/74debf751b71f1118c2d863d917678bd8987743a.png"),
    // 여드름 난 주황머리 백인
    ACNE_ORANGE_HAIR_BOY("26", "https://speako-bucket.s3.ap-northeast-2.amazonaws.com/profile-image/72f71c481924a99473c91bfdac585c9cc9c2bc58.png"),
    // 고양이 3마리와 키보드 치는 남성
    CAT_GUY_TYPING("28", "https://speako-bucket.s3.ap-northeast-2.amazonaws.com/profile-image/d8299e7fa3e2416b8b9504bb2da108cbcd225007.png"),
    // 노란 비니, 초록 스프레이 여성
    BEANIE_GIRL_SPRAY("29", "https://speako-bucket.s3.ap-northeast-2.amazonaws.com/profile-image/d5fb6bc139a3da5bc43ab0601942a4cf33722fa1.png"),
    // 양갈래머리, 스피커폰 소녀
    PIGTAIL_GIRL_PHONE("30", "https://speako-bucket.s3.ap-northeast-2.amazonaws.com/profile-image/c28810f772a98a2e7933d4dec7904c11bf11ad82.png");

    private final String displayNumber;
    @Getter
    private final String imageUrl;

    ImageType(String displayNumber, String imageUrl) {
        this.displayNumber = displayNumber;
        this.imageUrl = imageUrl;
    }

    @JsonValue
    public String getDisplayNumber() {
        return displayNumber;
    }

    // 입력 값과 일치하는 displayNumber의 ImageType 찾기
    public static ImageType fromDisplayNumber(String displayNumber) {

        for (ImageType imageType : ImageType.values()) {
            if (imageType.getDisplayNumber().equals(displayNumber)) {
                return imageType;
            }
        }
        throw new IllegalArgumentException("Unknown image Number: " + displayNumber);
    }

    // 랜덤 ImageType 반환
    public static ImageType getRandom() {

        ImageType[] values = ImageType.values();
        int randomIndex = ThreadLocalRandom.current().nextInt(values.length);
        return values[randomIndex];
    }
}
