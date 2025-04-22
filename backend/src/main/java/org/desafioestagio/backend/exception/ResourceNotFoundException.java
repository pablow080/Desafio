package org.desafioestagio.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando um recurso não é encontrado no sistema.
 *
 * Esta exceção automaticamente retorna um status HTTP 404 (NOT FOUND)
 * quando lançada em um controller Spring.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constrói uma nova exceção com a mensagem especificada.
     *
     * @param message a mensagem detalhando a exceção
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

}