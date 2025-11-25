package io.mlqs.memory.db;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
/**
 * redis缓存记忆实现记忆的快速查找
 * 缓存的key为sessionId
 * 缓存的value为Memory类
 */
public class MemoryCache {
    private static final Marker REIDS_MARKER = MarkerFactory.getMarker("REDIS");
    @Autowired
    private RedisTemplate<String, Object> rt;

    //过期时间，是代理存活时间的2倍
    @Value("${mlqs.agent.timeout}")
    private Long timeout;

    public void set(String key, String value) {
        //缓存的过期时间为300秒
        rt.opsForValue().set(key, value ,timeout * 2, TimeUnit.SECONDS);
    }

    public List<ChatMessage> get(String key) {
        String json = (String) rt.opsForValue().get(key);
        if (json != null) {
            //刷新过期时间
            rt.expire(key, timeout * 2, TimeUnit.SECONDS);
            return ChatMessageDeserializer.messagesFromJson(json);
        }
        return null;
    }

    public int size() {
        return rt.keys("*").size();
    }

    public void remove(String key) {
        rt.delete(key);
    }

    //获取迭代器
    public Iterable<String> Iterator() {
        return rt.keys("*");
    }

    //设置定时任务输出缓存条目，每分钟执行
    @Scheduled(cron = "${mlqs.log.report-cron}")
    public void schedule() {
        log.debug(REIDS_MARKER, "缓存(Redis)条目数：" + size());
    }
}
