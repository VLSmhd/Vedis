package com.vls.cache.core.support.persist;

import com.github.houbb.heaven.util.lang.StringUtil;
import com.vls.cache.api.ICache;
import com.vls.cache.api.ICachePersist;
import com.vls.cache.util.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CachePersistAof<K,V> implements ICachePersist<K,V> {

    private final String filePath;

    private final List<String> bufferList;

    public CachePersistAof(String filePath) {
        this.filePath = filePath;
        bufferList = new ArrayList<>();
    }


    @Override
    public void persist(ICache<K, V> cache) {
        log.info("开始 AOF 持久化到文件");
        if(!FileUtils.exists(filePath)){
            FileUtils.createFile(filePath);
        }
        FileUtils.append(filePath, bufferList);

        bufferList.clear();
        log.info("完成 AOF 持久化到文件");
    }

    @Override
    public long delay() {
        return 1;
    }

    @Override
    public long period() {
        return 1;
    }

    @Override
    public TimeUnit timeUnit() {
        return TimeUnit.SECONDS;
    }


    public void appendBuffer(String json){
        if(!StringUtil.isEmpty(json)){
            bufferList.add(json);
        }
    }
}
