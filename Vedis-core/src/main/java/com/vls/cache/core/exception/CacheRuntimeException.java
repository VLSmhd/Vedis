package com.vls.cache.core.exception;

import com.vls.cache.core.guide.CacheGuide;

/**
 * @description: TODO
 * @author VLS
 * @date 2023/12/4 16:27
 * @version 1.0
 *
 */
public class CacheRuntimeException extends RuntimeException{
    public CacheRuntimeException() {
    }

    public CacheRuntimeException(String message) {
        super(message);
    }

    public CacheRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheRuntimeException(Throwable cause) {
        super(cause);
    }

    public CacheRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);

    }
}
