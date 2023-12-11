package com.vls.cache.core.support.load;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.houbb.heaven.util.io.FileUtil;
import com.github.houbb.heaven.util.lang.StringUtil;
import com.github.houbb.heaven.util.util.CollectionUtil;
import com.vls.cache.annotation.CacheInterceptor;
import com.vls.cache.api.ICache;
import com.vls.cache.api.ICacheLoad;
import com.vls.cache.core.Cache;
import com.vls.cache.core.model.PersistAofEntry;
import com.vls.cache.util.FileUtils;
import com.vls.cache.util.ReflectMethodUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CacheLoadAof<K,V> implements ICacheLoad<K,V> {

    private final String filePath;

    public CacheLoadAof(String filePath) {
        this.filePath = filePath;
    }

    public static final Map<String, Method> METHOD_MAP ;

    //初始化METHOD_MAP,这里是统计添加了CacheInterceptor拦截器的全部方法
    static {
        METHOD_MAP = new HashMap<>();
        Method[] methods = Cache.class.getMethods();

        for (Method method : methods) {
            CacheInterceptor cacheInterceptor = method.getAnnotation(CacheInterceptor.class);
            if(cacheInterceptor != null){
                if(cacheInterceptor.aof()){
                    METHOD_MAP.put(method.getName(), method);
                }
            }
        }

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

            PersistAofEntry aofEntry = JSON.parseObject(line, PersistAofEntry.class);

            String methodName = aofEntry.getMethodName();
            Object[] params = aofEntry.getParams();
            Method method = METHOD_MAP.get(methodName);
            try {
                method.invoke(cache, params);
            } catch (IllegalAccessException| InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }
}
