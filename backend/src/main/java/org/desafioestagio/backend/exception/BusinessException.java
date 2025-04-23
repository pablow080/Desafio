package org.desafioestagio.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {

    private final String errorCode;
    private final Object[] args;
    public BusinessException(String message) {
        super(message);
        this.errorCode = null;
        this.args = null;
    }
}