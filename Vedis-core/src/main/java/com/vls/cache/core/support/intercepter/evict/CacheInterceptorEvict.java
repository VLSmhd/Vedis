package com.vls.cache.core.support.intercepter.evict;

import com.vls.cache.api.ICache;
import com.vls.cache.api.ICacheEvict;
import com.vls.cache.api.ICacheInterceptor;
import com.vls.cache.api.ICacheInterceptorContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class CacheInterceptorEvict<K,V> implements ICacheInterceptor<K,V> {


    @Override
    public void before(ICacheInterceptorContext<K, V> context) {

    }

    /*
     * 额外配置lru功能
     * @param
     * @return
     */
    @Override
    public void after(ICacheInterceptorContext<K, V> context) {
        ICache<K, V> cache = context.cache();
        ICacheEvict<K, V> evict = cache.evict();

        Method method = context.method();
        Object[] params = context.params();
        K key = (K)params[0];
        if("remove".equals(method.getName())){
            evict.removeKey(key);
        }else{
            evict.updateKey(key);
        }
    }
}
