package com.github.carsymor.lucene.exceptions;

/**
 * 标识无法直接转换为String的值类型的异常
 *
 * @author Carsymor
 */
public class UnSupportValueTypeException extends RuntimeException {

    public UnSupportValueTypeException(String message) {
        super(message);
    }

    public UnSupportValueTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
