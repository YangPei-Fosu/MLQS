package io.mlqs.memory;

import dev.langchain4j.memory.ChatMemory;


/**
 * 主要的记忆接口
 * 实现对记忆的处理
 */
public interface MemoryService {
    /**
     * 创建一个记忆，注意只创建缓存供Agent完成初次对话，后续再由其他方式持久化
     * @return sessionId
     */
    String createMemory();
    ChatMemory getMemory(String sessionId);
    void updateMemory(String sessionId, ChatMemory chatMemory);
    void saveMemory(String sessionId);
    void clearMemory(String sessionId);
    void deleteMemory(String sessionId);
    void deleteMemoryCache(String sessionId);
}
