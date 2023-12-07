package com.vls.cache.core.support.evict;

import com.vls.cache.api.ICacheEntry;
import com.vls.cache.api.ICacheEvict;
import com.vls.cache.api.ICacheEvictContext;

/**
 * @description: 无淘汰策略
 * @author VLS
 * @date 2023/12/4 20:47
 * @version 1.0
 */
public class CacheEvictNone<K,V> implements ICacheEvict<K,V> {
    @Override
    public ICacheEntry<K,V> evict(ICacheEvictContext<K,V> context) {
        return null;
    }
}
