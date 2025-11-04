package io.mlqs.memory;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.ChatMemory;

public interface MemoryService {
    ChatMemory getMemory(String sessionId);
    void updateMemory(String sessionId, ChatMemory chatMemory);
    void saveMemory(String sessionId);
    void clearMemory(String sessionId);
    void deleteMemory(String sessionId);
}
