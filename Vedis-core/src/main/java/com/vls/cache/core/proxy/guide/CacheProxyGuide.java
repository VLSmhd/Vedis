package com.vls.cache.core.proxy.guide;

import com.vls.cache.annotation.CacheInterceptor;
import com.vls.cache.api.ICache;
import com.vls.cache.api.ICacheInterceptor;
import com.vls.cache.api.ICachePersist;
import com.vls.cache.core.support.intercepter.CacheInterceptorContext;
import com.vls.cache.core.support.intercepter.CacheInterceptors;
import com.vls.cache.core.support.persist.CachePersistAof;

import java.util.List;

public class CacheProxyGuide {

    private CacheProxyGuide(){}

    private ICacheProxyGuideContext context;

    public CacheProxyGuide setContext(ICacheProxyGuideContext context) {
        this.context = context;
        return this;
    }

    public static CacheProxyGuide newInstance(){
        return new CacheProxyGuide();
    }


    /**
     * 持久化拦截器
     * @since 0.0.10
     */
    @SuppressWarnings("all")
    private final ICacheInterceptor persistInterceptors = CacheInterceptors.aof();


    private final List<ICacheInterceptor> costInterceptors = CacheInterceptors.defaultCosts();

    private final ICacheInterceptor evictInterceptor = CacheInterceptors.evict();


    /*
     * 启动代理增强   这里其实就是拦截器增强的流程  before  方法  after
     * @param
     * @return 代理的方法的执行结果
     */
    public Object execute() throws Throwable{
        long startMills = System.currentTimeMillis();
        ICache cache = context.target();
        CacheInterceptorContext interceptorContext = CacheInterceptorContext.newInstance()
                .setStartMills(startMills)
                .setMethod(context.method())
                .setParams(context.params())
                .setCache(cache);

        //1. 获取刷新注解信息
        CacheInterceptor interceptor = context.interceptor();
        //先执行拦截器的before
        this.interceptorHandler(interceptor, interceptorContext, cache, true);

        //加了注解的方法的执行   真正执行方法的是代理，不可能是拦截器，所以不要调用CacheInterceptorContext.result
        Object result = context.invoke();
        long endMills = System.currentTimeMillis();

        //把剩余信息提供给拦截器
        interceptorContext.setEndMills(endMills).setResult(result);

        //再执行拦截器的after
        this.interceptorHandler(interceptor, interceptorContext, cache, false);

        return result;
    }


    private void interceptorHandler(CacheInterceptor interceptor,
                                    CacheInterceptorContext interceptorContext,
                                    ICache cache,
                                    boolean before){

        if(interceptor != null){
            //cost统计方法时长
            if(interceptor.common()){
                for (ICacheInterceptor costInterceptor : costInterceptors) {
                    if(before){
                        costInterceptor.before(interceptorContext);
                    }else{
                        costInterceptor.after(interceptorContext);
                    }
                }
            }

            if(interceptor.evict()){
                if(before){
                    evictInterceptor.before(interceptorContext);
                }else{
                    evictInterceptor.after(interceptorContext);
                }
            }


            //aof追加
            ICachePersist persist = cache.persist();
            if(interceptor.aof() && (persist instanceof CachePersistAof)){
                if(before){
                    persistInterceptors.before(interceptorContext);
                }else{
                    persistInterceptors.after(interceptorContext);
                }
            }



        }

    }
}
