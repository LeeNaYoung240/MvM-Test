package com.sparta.mvm.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorEnum statusEnum;

    public CustomException(ErrorEnum statusEnum) {
        super(statusEnum.getMsg());
        this.statusEnum = statusEnum;
    }
}
