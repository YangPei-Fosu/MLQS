package io.mlqs.agent;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import io.mlqs.es.AiToolService;
import io.mlqs.memory.MemoryProvider;
import io.mlqs.memory.MemoryService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Data
@Service
@Slf4j
public class AgentServiceImpl implements AgentService , SmartLifecycle {
    private static final Marker AGENT_MARKER = MarkerFactory.getMarker("AGENT");

    //代理集合，key为sessionId，value为代理对象
    private Map<String, Agent> agentMap = new ConcurrentHashMap<>();

    //对话记忆处理接口
    @Autowired
    private MemoryService memoryService;

    /**
     * 代理存活时间，如果超过指定时间认为代理已失效，则保存记忆并删除代理与相关的记忆缓存
     * key为sessionId，value为时间戳，单位秒
     */
    private Map<String, Timestamp> agentAccessTimes = new ConcurrentHashMap<>();

    //使用的语言模型
    @Autowired
    private ChatLanguageModel model;
    //记忆提供接口
    @Autowired
    private MemoryProvider memoryProvider;
    //代理存活时间，在yml中设置
    @Value("${mlqs.agent.timeout}")
    private Long timeout;

    //prompt
    @Autowired
    private Prompt prompt;

    //Ai代理使用的工具接口
    @Autowired
    private AiToolService aiToolService;

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
                    .systemMessageProvider(mem -> prompt.getChatPrompt())
                    //工具
                    .tools(aiToolService)
                    .build();
            return build;
            }
        );
        //更新代理访问时间
        agentAccessTimes.put(sessionId, new Timestamp(System.currentTimeMillis()));
        return agent;
    }

    //删除代理对象，意味着代理已失效，写入Mysql
    @Override
    public void remove(String sessionId) {
        //写入Mysql
        memoryService.saveMemory(sessionId);
        //删除代理对象
        agentMap.remove(sessionId);
        agentAccessTimes.remove(sessionId);
    }

    //每10秒检查代理存活时间，超过指定时间则删除代理对象
    @Scheduled(fixedDelay = 10000)
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

    @Scheduled(cron = "${mlqs.log.report-cron}")
    public void schedule() {
        //输出信息
        log.debug(AGENT_MARKER, "存活的代理数：" + agentMap.size());
    }

    /**
     * 结束进程时运行
     * 若进城结束时若有代理存活则保存信息到Mysql中
     */
    private volatile boolean running = true;

    //销毁
    public void destroy() {
        try {
            if(agentMap.size() > 0){
                log.info(AGENT_MARKER, "正在存储全部代理信息。共" + agentMap.size() + "条数据");
                for (String sessionId : agentMap.keySet()) {
                    remove(sessionId);
                    //同时移除缓存内的记录
                    memoryService.deleteCache(sessionId);
                }
            }
            log.info(AGENT_MARKER, "全部代理信息存储完成。");
        }catch (Exception e){
            log.error(AGENT_MARKER, "存储失败。");
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        running = true;
    }

    @Override
    public void stop() {
        destroy();
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
