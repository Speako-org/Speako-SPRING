package com.speako.domain.user.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum ImageType {

    DEFAULT("default", "https://your-cdn.com/images/default.png"),
    DOG("dog", "https://your-cdn.com/images/dog.png"),
    CAT("cat", "https://your-cdn.com/images/cat.png"),
    FOX("fox", "https://your-cdn.com/images/fox.png");

    private final String displayName;
    @Getter
    private final String imageUrl;

    ImageType(String displayName, String imageUrl) {
        this.displayName = displayName;
        this.imageUrl = imageUrl;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    // 입력 값과 일치하는 displayName의 ImageType 찾기
    public static ImageType fromDisplayName(String displayName) {
        for (ImageType imageType : ImageType.values()) {
            if (imageType.getDisplayName().equalsIgnoreCase(displayName)) {
                return imageType;
            }
        }
        throw new IllegalArgumentException("Unknown image name: " + displayName);
    }
}
