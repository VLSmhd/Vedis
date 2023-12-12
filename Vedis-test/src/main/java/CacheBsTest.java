import com.vls.cache.api.ICache;
import com.vls.cache.core.guide.CacheGuide;
import com.vls.cache.core.support.evict.CacheEvicts;
import com.vls.cache.core.support.listener.slow.CacheSlowListener;
import com.vls.cache.core.support.listener.slow.CacheSlowListeners;
import com.vls.cache.core.support.load.CacheLoads;
import com.vls.cache.core.support.persist.CachePersists;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * 缓存引导类测试
 * @author binbin.hou
 * @since 0.0.2
 */
public class CacheBsTest {

    /**
     * 大小指定测试
     * @since 0.0.2
     */
    @Test
    public void helloTest() {
        ICache<String, String> cache = CacheGuide.<String,String>newInstance()
                .sizeLimit(2)
                .cacheEvict(CacheEvicts.<String, String>fifo())
                .build();

        cache.put("1", "1");
        cache.put("2", "2");
        cache.put("3", "3");
        cache.put("4", "4");

        System.out.println(cache.keySet());
    }


    /**
     * 过期测试
     * @since 0.0.3
     */
    @Test
    public void expireTest() throws InterruptedException {
        ICache<String, String> cache = CacheGuide.<String,String>newInstance()
                .sizeLimit(3)
                .build();

        cache.put("1", "1");
        cache.put("2", "2");

        cache.expire("1", 50, TimeUnit.MILLISECONDS);
        System.out.println(cache.keySet());
        Assert.assertEquals(2, cache.size());

        TimeUnit.MILLISECONDS.sleep(200);
        Assert.assertEquals(1, cache.size());
        System.out.println(cache.keySet());
    }


    @Test
    public void rdbTest() throws InterruptedException {
        ICache<String, String> cache = CacheGuide.<String, String>newInstance()
                .load(CacheLoads.<String, String>fileJson("test1.rdb"))
                .build();

        Assert.assertEquals(2, cache.size());
        TimeUnit.SECONDS.sleep(2000);
    }

    @Test
    public void slowLogTest() {
        ICache<String, String> cache = CacheGuide.<String,String>newInstance()
                .addSlowListener(CacheSlowListeners.defaults())
                .build();

        cache.put("1", "2");
        cache.get("1");
    }

    @Test
    public void aofTest() throws InterruptedException {
        ICache<String, String> cache = CacheGuide.<String,String>newInstance()
                .persist(CachePersists.<String, String>aof("1.aof"))
                .build();
        cache.put("1", "1");
        cache.expireAt("1", 10);
        cache.remove("2");
        TimeUnit.SECONDS.sleep(100);
    }

    @Test
    public void aofLoadTest() {
        ICache<String, String> cache = CacheGuide.<String,String>newInstance()
                .load(CacheLoads.<String, String>aof("1.aof"))
                .build();

        Assert.assertEquals(1, cache.size());
        System.out.println(cache.keySet());
    }



    @Test
    public void lruTest() {
        ICache<String, String> cache = CacheGuide.<String,String>newInstance()
                .sizeLimit(3)
                .evict(CacheEvicts.<String, String>lru())
                .build();
        cache.put("A", "hello");
        cache.put("B", "world");
        cache.put("C", "FIFO");

// 访问一次A
        cache.get("A");
        System.out.println(cache.keySet());
        cache.remove("B");
        cache.put("D", "LRU");
        cache.put("E", "wqe");
        cache.put("F", "asd");

        Assert.assertEquals(3, cache.size());

        System.out.println(cache.keySet());
    }

    @Test
    public void lru2QTest() {
        ICache<String, String> cache = CacheGuide.<String,String>newInstance()
                .sizeLimit(3)
                .evict(CacheEvicts.<String, String>lru2Q())
                .build();

        cache.put("A", "hello");
        cache.put("B", "world");
        cache.put("C", "FIFO");

        // 访问一次A
        cache.get("A");
        cache.put("D", "LRU");
        cache.put("E", "LRU");

        Assert.assertEquals(3, cache.size());
        System.out.println(cache.keySet());
    }
    @Test
    public void lru2Test() {
        ICache<String, String> cache = CacheGuide.<String,String>newInstance()
                .sizeLimit(3)
                .evict(CacheEvicts.<String, String>lru2())
                .build();
        cache.put("A", "hello");
        cache.put("B", "world");
        cache.put("C", "FIFO");
        // 访问一次A
        cache.get("A");
        cache.put("D", "LRU");
        Assert.assertEquals(3, cache.size());
        System.out.println(cache.keySet());
    }
    @Test
    public void lfuTest() {
        ICache<String, String> cache = CacheGuide.<String,String>newInstance()
                .sizeLimit(3)
                .evict(CacheEvicts.<String, String>lfu())
                .build();
        cache.put("A", "hello");
        cache.get("A");
        cache.get("A");
        cache.put("B", "world");
        cache.get("B");
        cache.put("C", "bug");
        cache.put("D", "bug");

//        cache.put("C", "FIFO");
//// 访问一次A
//        cache.get("A");
//        cache.put("D", "LRU");
//        cache.put("E", "LRU");
//        cache.put("F", "LRU");

        Assert.assertEquals(3, cache.size());
        System.out.println(cache.keySet());
    }

}