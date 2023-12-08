package com.vls.cache.core.support.intercepter.aof;

import com.alibaba.fastjson.JSON;
import com.vls.cache.api.ICache;
import com.vls.cache.api.ICacheInterceptor;
import com.vls.cache.api.ICacheInterceptorContext;
import com.vls.cache.api.ICachePersist;
import com.vls.cache.core.model.PersistAofEntry;
import com.vls.cache.core.support.persist.CachePersistAof;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class CacheInterceptorAof<K,V> implements ICacheInterceptor<K,V> {

    @Override
    public void before(ICacheInterceptorContext<K, V> context) {
    }

    @Override
    public void after(ICacheInterceptorContext<K, V> context) {
        ICache<K, V> cache = context.cache();
        ICachePersist<K, V> persist = cache.persist();
        //判断持久化策略
        if(persist instanceof CachePersistAof){
            Method method = context.method();
            Object[] params = context.params();

            PersistAofEntry aofEntry = PersistAofEntry.of(method.getName(), params);
            String aofEntryJSON = JSON.toJSONString(aofEntry);

            //先添加到buffer
            CachePersistAof cachePersistAof = (CachePersistAof) persist;

            log.debug("AOF 开始追加文件内容：{}", aofEntryJSON);
            cachePersistAof.appendBuffer(aofEntryJSON);
            log.debug("AOF 完成追加文件内容：{}", aofEntryJSON);
        }

    }
}
