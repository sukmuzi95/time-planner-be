package com.timeplanner.dev.global.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiException extends RuntimeException {

    private HttpStatus status;
    private String message;

    @Builder
    public ApiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
    }
}
