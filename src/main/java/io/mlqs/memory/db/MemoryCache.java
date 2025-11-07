package io.mlqs.memory.db;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import io.mlqs.utils.LogUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * redis缓存记忆实现记忆的快速查找
 * 缓存的key为sessionId
 * 缓存的value为Memory类
 */
public class MemoryCache {
    @Autowired
    private RedisTemplate<String, Object> rt;

    //过期时间，比代理的超时时间长30秒
    @Value("${agent.timeout}")
    private Long timeout;

    public void set(String key, String value) {
        //缓存的过期时间为300秒
        rt.opsForValue().set(key, value ,timeout + 30, TimeUnit.SECONDS);
    }

    public List<ChatMessage> get(String key) {
        String json = (String) rt.opsForValue().get(key);
        if (json != null) {
            //刷新过期时间
            rt.expire(key, timeout + 30, TimeUnit.SECONDS);
            return ChatMessageDeserializer.messagesFromJson(json);
        }
        return new ArrayList<>();
    }

    public void remove(String key) {
        rt.delete(key);
    }

    //设置定时任务输出缓存条目，每分钟执行
    @Scheduled(cron = "${log.report-cron}")
    public void schedule() {
        LogUtils.report(MemoryCache.class, "Redis" ,"缓存条目数：" + rt.keys("*").size());
    }
}
