package com.speako.domain.user.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserGender {

    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other");

    private final String displayName;

    UserGender(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }
}
