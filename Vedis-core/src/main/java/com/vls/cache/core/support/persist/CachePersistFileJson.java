package com.vls.cache.core.support.persist;

import com.alibaba.fastjson.JSON;
import com.github.houbb.heaven.util.io.FileUtil;
import com.vls.cache.api.ICache;
import com.vls.cache.api.ICachePersist;
import com.vls.cache.core.model.PersistRdbEntry;
import com.vls.cache.util.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;

/**
 * @description: JSON形式 RDB持久化
 * @author VLS
 * @date 2023/12/5 20:49
 * @version 1.0
 */
public class CachePersistFileJson<K,V> implements ICachePersist<K,V> {

    private final String filePath;

    public CachePersistFileJson(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void persist(ICache<K, V> cache) {
        Set<Map.Entry<K, V>> entries = cache.entrySet();

        FileUtils.createFile(filePath);
        //清空文件内容
        FileUtils.truncateFile(filePath);

        for (Map.Entry<K, V> entry : entries) {
            K key = entry.getKey();
            Long expireTime = cache.expire().expireTime(key);
            PersistRdbEntry<K,V> rdbEntry = new PersistRdbEntry<>();
            rdbEntry.setKey(key);
            rdbEntry.setValue(entry.getValue());
            rdbEntry.setExpire(expireTime);

            String line = JSON.toJSONString(rdbEntry);
            FileUtil.write(filePath, line, StandardOpenOption.APPEND);
        }


    }


}
