package org.desafioestagio.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando ocorre uma violação das regras de negócio.
 * <p>
 * Esta exceção automaticamente retorna um status HTTP 400 (BAD REQUEST)
 * quando lançada em um controller Spring.
 */
@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {

    /**
     * -- GETTER --
     *
     */
    private final String errorCode;
    /**
     * -- GETTER --
     *
     */
    private final Object[] args;

    /**
     * Constrói uma nova exceção com a mensagem especificada.
     *
     * @param message a mensagem detalhando a exceção
     */
    public BusinessException(String message, String errorCode, Object[] args) {
        super(message);
        this.errorCode = errorCode;
        this.args = args;
    }

    public BusinessException(String message) {
        super(message);
        this.errorCode = null;
        this.args = null;
    }
}