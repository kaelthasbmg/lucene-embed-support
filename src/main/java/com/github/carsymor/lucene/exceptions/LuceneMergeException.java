package com.github.carsymor.lucene.exceptions;

/**
 * 合并索引失败时抛出异常
 */
public class LuceneMergeException extends RuntimeException {

    public LuceneMergeException(String message, Throwable cause) {
        super(message, cause);
    }
}
