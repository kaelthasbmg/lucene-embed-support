package com.github.kaelthasbmg.lucene.exceptions;

/**
 * 参数错误时抛出异常
 *
 * @author Carsymor
 */
public class InvalidParameterException extends RuntimeException {

    public InvalidParameterException(String message) {
        super(message);
    }

    public InvalidParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}
