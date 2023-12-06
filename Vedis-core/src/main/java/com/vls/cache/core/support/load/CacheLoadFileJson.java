package com.vls.cache.core.support.load;

import com.alibaba.fastjson.JSONObject;
import com.github.houbb.heaven.util.io.FileUtil;
import com.github.houbb.heaven.util.lang.StringUtil;
import com.github.houbb.heaven.util.util.CollectionUtil;
import com.vls.cache.api.ICache;
import com.vls.cache.api.ICacheLoad;
import com.vls.cache.core.model.PersistRdbEntry;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @description: JSON持久化加载实现
 * @author VLS
 * @date 2023/12/5 21:47
 * @version 1.0
 */
@Slf4j
public class CacheLoadFileJson<K,V> implements ICacheLoad<K,V> {

    private final String filePath;

    public CacheLoadFileJson(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void load(ICache<K, V> cache) {
        List<String> lines = FileUtil.readAllLines(filePath);
        log.debug("[load] 开始加载处理 path: {}", filePath);
        if(CollectionUtil.isEmpty(lines)) {
            log.info("[load] path: {} 文件内容为空，直接返回", filePath);
            return;
        }

        for (String line : lines) {
            if(StringUtil.isEmpty(line)){
                continue;
            }

            PersistRdbEntry<K,V> rdbEntry = JSONObject.parseObject(line, PersistRdbEntry.class);
            K key = rdbEntry.getKey();
            V value = rdbEntry.getValue();
            Long expire = rdbEntry.getExpire();

            cache.put(key, value);

            if(expire != null){
                cache.expireAt(key, expire);
            }

        }
        log.debug("[load] 加载完成  path: {}", filePath);
    }
}
