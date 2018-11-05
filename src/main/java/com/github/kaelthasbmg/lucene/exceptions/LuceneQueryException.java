package com.github.kaelthasbmg.lucene.exceptions;

/**
 * Lucene查询失败时抛出的异常
 *
 * @author Carsymor
 */
public class LuceneQueryException extends RuntimeException {
    public LuceneQueryException(String message) {
        super(message);
    }

    public LuceneQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
