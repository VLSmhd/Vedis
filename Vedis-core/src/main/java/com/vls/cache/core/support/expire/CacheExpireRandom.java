package com.vls.cache.core.support.expire;


import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.houbb.heaven.util.util.MapUtil;
import com.vls.cache.api.ICache;
import com.vls.cache.api.ICacheExpire;
import com.vls.cache.api.ICacheRemoveListener;
import com.vls.cache.constant.enums.CacheRemoveType;
import com.vls.cache.core.exception.CacheRuntimeException;
import com.vls.cache.core.support.listener.remove.CacheRemoveListenerContext;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.core.CollectionUtils;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CacheExpireRandom<K,V> implements ICacheExpire<K,V> {

    private static final int COUNT_LIMIT = 100;
    //是否启用快模式
    private volatile boolean fastMode = false;

    private final Map<K, Long> expireMap = new HashMap<>();

    private final ICache<K,V> cache;

    public CacheExpireRandom(ICache<K, V> cache) {
        this.cache = cache;
        this.init();
    }

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    private void init() {
        EXECUTOR_SERVICE.scheduleAtFixedRate(new ExpireRandomThread(), 10, 10, TimeUnit.SECONDS);
    }

    private class ExpireRandomThread implements Runnable {

        @Override
        public void run() {
            if(MapUtil.isEmpty(expireMap)) {
                log.info("expireMap 信息为空，直接跳过本次处理。");
                return;
            }
            if(fastMode) {
                expireKeys(10L);

            }

            expireKeys(100L);

        }
    }

    private void expireKeys(long timeMills) {
        long timeLimit = timeMills + System.currentTimeMillis();

        this.fastMode = false;
        int count = 0;
        while(true) {

            if(count >= COUNT_LIMIT) {
                log.info("过期淘汰次数已经达到最大次数: {}，完成本次执行。", COUNT_LIMIT);
                return;
            }

            if(System.currentTimeMillis() >= timeLimit) {
                fastMode = true;
                log.debug("过期淘汰已经达到限制时间，中断本次执行，设置 fastMode=true");
                return;
            }

            K key = getRandomKeyWithList();
            boolean isCleaned = cleanExpireKey(key);
            log.debug("key: {} 过期执行结果 {}", key, isCleaned);
            count++;
        }
    }

    private K getRandomKeyWithList() {
        Random random = ThreadLocalRandom.current();
        ArrayList<K> list = new ArrayList<>(expireMap.keySet());
        int randomIdx = random.nextInt(list.size());
        return list.get(randomIdx);
    }


    private K getRandomKeyWithIterator() {
        Random random = ThreadLocalRandom.current();
        int randomIdx = random.nextInt(expireMap.size());
        Iterator<K> iterator = expireMap.keySet().iterator();
        int count = 0;
        while(iterator.hasNext()) {
            if(count == randomIdx) {
                return iterator.next();
            }
            count++;
        }
        throw new CacheRuntimeException("Random 获取对应key: [error] 信息不存在");
    }

    private static int BATCH_RANDOM_KEY_SIZE_LIMIT = 50;

    private Set<K> getRandomKeyBatch() {
        Random random = ThreadLocalRandom.current();
        int randomIdx = random.nextInt(expireMap.size());
        Iterator<K> iterator = expireMap.keySet().iterator();
        int count = 0;
        Set<K> keySet = new HashSet<>();
        //一次性最多取50个数据
        while (iterator.hasNext()) {
            if(keySet.size() >= BATCH_RANDOM_KEY_SIZE_LIMIT) {
                return keySet;
            }

            K curKey = iterator.next();

            if(count > randomIdx - 50 && count <= randomIdx + 50) {
                keySet.add(curKey);
            }
            count++;

        }
        return keySet;
    }

    /*
     * @param expireAt:设置的过期时间
     * @return
     */
    private boolean cleanExpireKey(K key){
        Long expireAtMs = expireMap.get(key);
        long currentTimeMillis = System.currentTimeMillis();

        try {
            if(currentTimeMillis >= expireAtMs){
                expireMap.remove(key);
                V removeValue = cache.remove(key);

                //过期监听器
                CacheRemoveListenerContext<K, V> cacheRemoveListenerContext = CacheRemoveListenerContext.<K, V>newInstance()
                        .setKey(key)
                        .setValue(removeValue)
                        .setType(CacheRemoveType.EXPIRE.getCode());

                for (ICacheRemoveListener<K, V> removeListener : cache.removeListeners()) {
                    removeListener.listen(cacheRemoveListenerContext);
                }

            }
        } catch (Exception e) {
            log.debug("{}",e);
            return false;
        }
        return true;
    }

    @Override
    public void expire(K key, long expireAt) {
        expireMap.put(key, expireAt);
    }

    @Override
    public void lazyRefresh(Collection<K> keyList) {
        if(CollectionUtil.isEmpty(keyList)) {
            return;
        }

        // 判断大小，小的作为外循环。一般都是过期的 keys 比较小。
        if(keyList.size() <= expireMap.size()) {
            for(K key : keyList) {
                cleanExpireKey(key);
            }
        } else {
            for(Map.Entry<K, Long> entry : expireMap.entrySet()) {
                cleanExpireKey(entry.getKey());
            }
        }
    }

    @Override
    public Long expireTime(K key) {
        return expireMap.get(key);
    }
}
