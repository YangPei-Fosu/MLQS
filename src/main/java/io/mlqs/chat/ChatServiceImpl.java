package io.mlqs.chat;

import io.mlqs.agent.AgentService;
import io.mlqs.memory.MemoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ChatServiceImpl implements ChatService{

    @Autowired
    private AgentService agentService;

    @Autowired
    private MemoryService memoryService;

    /**
     * 交互接口，如果传入的sessionId为空，则相当于创建新的会话
     * @param sessionId
     * @param context
     * @return 有两个键值对【sessionId, sessionId的值】【context, Ai的回复】
     */
    @Override
    public Map<String,String> chat(String sessionId, String context) {
        boolean isNew = sessionId == null;
        //如果是新会话就先创建缓存使得代理先完成第一次会话
        if(isNew)
            sessionId = memoryService.createMemory();
        //获得sessionId对应的代理，由代理负责处理会话，自动写入缓存
        String chat = agentService.get(sessionId).chat(sessionId, context);
        Map<String,String> result = new HashMap<>();
        result.put("sessionId", sessionId);
        result.put("context", chat);
        //新会话在完成第一次会话后直接持久化
        if(isNew)
            memoryService.saveMemory(sessionId);
        return result;
    }

    //删除整个会话
    @Override
    public void deleteChat(String sessionId) {
        //删除代理
        agentService.remove(sessionId);
        memoryService.deleteMemory(sessionId);
    }
}
