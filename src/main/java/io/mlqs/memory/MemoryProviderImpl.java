package io.mlqs.memory;

import dev.langchain4j.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemoryProviderImpl implements MemoryProvider{
    @Autowired
    private MemoryService memoryService;
    @Override
    public ChatMemory get(Object o) {
        String sessionId = o.toString();
        return memoryService.getMemory(sessionId);
    }
}
