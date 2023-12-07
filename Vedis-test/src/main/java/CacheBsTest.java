import com.vls.cache.api.ICache;
import com.vls.cache.core.guide.CacheGuide;
import com.vls.cache.core.support.evict.CacheEvicts;
import com.vls.cache.core.support.load.CacheLoads;
import com.vls.cache.core.support.load.MyCacheLoad;
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

}