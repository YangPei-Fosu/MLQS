package io.mlqs.agent;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;

/**
 * 代理接口
 */
public interface Agent {
    String chat(@MemoryId String sessionId, @UserMessage String userMessage);
}
