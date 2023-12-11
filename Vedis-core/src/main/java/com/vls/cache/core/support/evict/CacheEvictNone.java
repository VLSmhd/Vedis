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
public class CacheEvictNone<K,V> extends AbstractCacheEvict<K,V> {

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        return null;
    }
}
