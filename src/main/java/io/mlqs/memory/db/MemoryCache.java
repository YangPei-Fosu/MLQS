package io.mlqs.memory.db;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.ChatMemory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemoryCache {
    //<sessionId, 记忆类>
    private RedisTemplate<String, ChatMemory> rt;

    public void set(String key, ChatMemory value) {
        rt.opsForValue().set(key, value);
    }

    public ChatMemory get(String key) {
        return rt.opsForValue().get(key);
    }

    public void remove(String key) {
        rt.delete(key);
    }
}
