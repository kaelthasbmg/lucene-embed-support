package com.github.carsymor.lucene.exceptions;

/**
 * 索引写入异常
 *
 * @author Carsymor
 */
public class IndexWriteException extends RuntimeException {

    public IndexWriteException(String message, Throwable cause) {
        super(message, cause);
    }

    public IndexWriteException(Throwable cause) {
        super(cause);
    }
}
