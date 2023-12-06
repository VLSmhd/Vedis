package com.vls.cache.core.support.persist;

import com.vls.cache.api.ICache;
import com.vls.cache.api.ICachePersist;
import com.vls.cache.core.support.expire.CacheExpireSort;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @description: 定时执行持久化策略
 * @author VLS
 * @date 2023/12/5 21:35
 * @version 1.0
 */
@Slf4j
public class InnerCachePersist<K,V> {

    private ICache<K,V> cache;

    private ICachePersist<K,V> cachePersist;

    public InnerCachePersist(ICache<K, V> cache, ICachePersist<K, V> cachePersist) {
        this.cache = cache;
        this.cachePersist = cachePersist;

        this.init();
    }

    //单线程
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    /*
     * 初始化轮询任务
     * @param
     * @return
     */
    private void init(){
        EXECUTOR_SERVICE.scheduleAtFixedRate(new Runnable() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            @Override
            public void run() {
                log.debug("开始持久化......  time : {}", formatter.format(System.currentTimeMillis()));
                cachePersist.persist(cache);
                log.debug("完成持久化......  time : {}", formatter.format(System.currentTimeMillis()));
            }
        }, 0, 1, TimeUnit.MINUTES);
    }
}
