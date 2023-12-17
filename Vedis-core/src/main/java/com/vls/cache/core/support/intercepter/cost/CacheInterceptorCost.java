package com.vls.cache.core.support.intercepter.cost;

import com.github.houbb.heaven.util.util.CollectionUtil;
import com.vls.cache.api.ICacheInterceptor;
import com.vls.cache.api.ICacheInterceptorContext;
import com.vls.cache.api.ICacheSlowListener;
import com.vls.cache.core.support.listener.slow.CacheSlowListenerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
public class CacheInterceptorCost<K,V> implements ICacheInterceptor<K,V> {

    @Override
    public void before(ICacheInterceptorContext<K, V> context) {
        log.debug("Cost start, method: {}", context.method().getName());

    }

    @Override
    public void after(ICacheInterceptorContext<K, V> context) {
        long costMills = context.endMills() - context.startMills();
        String methodName = context.method().getName();
        log.debug("Cost end, method: {}, cost: {}ms", methodName, costMills);

        //添加慢日志操作
        List<ICacheSlowListener> slowListeners = context.cache().slowListeners();
        if(CollectionUtil.isNotEmpty(slowListeners)){
            CacheSlowListenerContext slowListenerContext = CacheSlowListenerContext.newInstance()
                    .setCostTimeMills(costMills)
                    .setEndTimeMills(context.endMills())
                    .setStartTimeMills(context.startMills())
                    .setMethodName(methodName)
                    .setParams(context.params())
                    .setResult(context.result());

            for (ICacheSlowListener slowListener : slowListeners) {
                //根据设置的阈值
                long slowerThanMills = slowListener.slowerThanMills();
                if(costMills > slowerThanMills){
                    slowListener.listen(slowListenerContext);
                }

            }
        }
    }
}
