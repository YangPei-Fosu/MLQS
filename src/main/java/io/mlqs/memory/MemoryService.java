package io.mlqs.memory;

import dev.langchain4j.memory.ChatMemory;


/**
 * 主要的记忆接口
 * 实现对记忆的处理
 */
public interface MemoryService {
    ChatMemory getMemory(String sessionId);
    void updateMemory(String sessionId, ChatMemory chatMemory);
    void saveMemory(String sessionId);
    void clearMemory(String sessionId);
    void deleteMemory(String sessionId);
    void deleteMemoryCache(String sessionId);
}
