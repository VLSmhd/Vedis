package com.vls.cache.annotation;

import java.lang.annotation.*;

@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface CacheInterceptor {

    /*
     * 是否开启aof持久化模式
     * @param
     * @return
     */
    boolean aof() default false;


    boolean common() default true;

    boolean evict() default false;
}
