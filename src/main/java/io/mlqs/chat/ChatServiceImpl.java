package io.mlqs.chat;

import io.mlqs.agent.AgentService;
import io.mlqs.memory.MemoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService{

    @Autowired
    private AgentService agentService;

    @Autowired
    private MemoryService memoryService;

    @Override
    public String chat(String sessionId, String context) {
        //获得sessionId对应的代理，由代理负责处理会话，自动写入缓存
        return agentService.get(sessionId).chat(sessionId, context);
    }

    //删除整个会话
    @Override
    public void deleteChat(String sessionId) {
        //删除代理
        agentService.remove(sessionId);
        memoryService.deleteMemory(sessionId);
    }
}
