package io.mlqs.agent;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import io.mlqs.memory.MemoryProvider;
import io.mlqs.memory.MemoryService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
@Service
public class AgentServiceImpl implements AgentService{
    //代理集合，key为sessionId，value为代理对象
    private Map<String, Agent> agentMap;

    //对话记忆处理接口
    @Autowired
    private MemoryService memoryService;

    /**
     * 代理存活时间，如果超过指定时间认为代理已失效，则保存记忆并删除代理与相关的记忆缓存
     * key为sessionId，value为时间戳，单位秒
     */
    private Map<String, Timestamp> agentAccessTimes;

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
    //代理存活时间，在yml中设置
    @Value("${agent.timeout}")
    private Long timeout;

    @Override
    public Agent get(String sessionId) {
        //在代理集合中查找代理对象，如果没有就创建一个代理
        Agent agent = agentMap.computeIfAbsent(sessionId, agt -> {
            //创建代理对象
            Agent build = AiServices.builder(Agent.class)
                    //模型
                    .chatLanguageModel(model)
                    //记忆管理
                    .chatMemoryProvider(memoryProvider)
                    //prompt
                    .systemMessageProvider(mem -> "")
                    //工具
                    .tools()
                    .build();
            return build;
            }
        );
        //更新代理访问时间
        agentAccessTimes.put(sessionId, new Timestamp(System.currentTimeMillis()));
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

    @PostConstruct
    public void init() {
        //初始化清理任务，每5分钟执行一次
        cleanupScheduler = Executors.newSingleThreadScheduledExecutor();
        cleanupScheduler.scheduleAtFixedRate(this::clearUpAgent, 5, 5, TimeUnit.MINUTES);
    }
    @PreDestroy
    public void destroy() {
        if (cleanupScheduler != null)
            cleanupScheduler.shutdown();
    }

    private void clearUpAgent(){
        //当前时间
        long currentTime = System.currentTimeMillis() / 1000;
        //若满足超时，则删除代理对象
        agentAccessTimes.entrySet().removeIf(entry -> {
            String sessionId = entry.getKey();
            //获取代理的上次访问时间，单位是秒
            Long accessTime = entry.getValue().getTime() / 1000;
            //判断是否超时
            if (currentTime - accessTime > timeout) {
                // 超时则清理相关资源
                remove(sessionId);
                return true;
            }
            return false;
        });
    }

}
