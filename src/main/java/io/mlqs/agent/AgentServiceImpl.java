package io.mlqs.agent;

import dev.langchain4j.model.chat.ChatLanguageModel;
import io.mlqs.memory.MemoryProvider;
import io.mlqs.memory.MemoryService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

@Data
public class AgentServiceImpl implements AgentService{
    //代理集合，key为sessionId，value为代理对象
    private Map<String, Agent> agentMap;

    //对话记忆处理接口
    @Autowired
    private MemoryService memoryService;

    /**
     * 代理存活时间，如果超过指定时间认为代理已失效，则保存记忆并删除代理与相关的记忆缓存
     * key为sessionId，value为时间戳，单位分钟
     */
    private Map<String, Long> agentAccessTimes;

    /**
     * 清理代理与记忆缓存的定时任务执行器
     * 每5分钟执行一次
     */
    private ScheduledExecutorService cleanupScheduler;

    //使用的语言模型
    private ChatLanguageModel model;
    //记忆提供接口
    @Autowired
    private MemoryProvider memoryProvider;

    @Override
    public Agent get(String sessionId) {
        //在代理集合中查找代理对象，如果没有就创建一个代理
        Agent agent = agentMap.computeIfAbsent(sessionId, agt ->
            dev.langchain4j.service.AiServices.builder(Agent.class)
                    //模型
                .chatLanguageModel(model)
                    //记忆管理
                .chatMemoryProvider(memoryProvider)
                    //prompt
                .systemMessageProvider(mem -> "")
                    //工具
                .tools()
                .build()
        );
        return agent;
    }

    //删除代理对象，意味着代理已失效，保存记忆并删除代理与相关的记忆缓存
    @Override
    public void remove(String sessionId) {
        memoryService.saveMemory(sessionId);
        memoryService.deleteMemoryCache(sessionId);
        agentMap.remove(sessionId);
        agentAccessTimes.remove(sessionId);
    }
}
