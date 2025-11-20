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

    /**
     * 获取一个记忆，先在缓存中获取，缓存中不存在则从Mysql中获取
     * @param sessionId
     * @return
     */
    ChatMemory getMemory(String sessionId);

    /**
     * 更新缓存
     * @param sessionId
     * @param chatMemory
     */
    void updateCache(String sessionId, ChatMemory chatMemory);

    /**
     * 保存一个记忆，先在缓存中获取记忆，然后保存到Mysql中
     * @param sessionId
     */
    void saveMemory(String sessionId);

    /**
     * 清空一个记忆，先清空在缓存中的记忆，然后保存到Mysql中
     * @param sessionId
     */
    void clearMemory(String sessionId);

    /**
     * 删除一个记忆，先删除缓存中的记忆，然后保存到Mysql中
     * @param sessionId
     */
    void deleteMemory(String sessionId);

    /**
     * 删除缓存中的记忆
     * @param sessionId
     */
    void deleteCache(String sessionId);
}
