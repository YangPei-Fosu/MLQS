package io.mlqs.agent;

/**
 * 代理服务接口
 */
public interface AgentService {
    //获取代理
    Agent get(String sessionId);
    //删除代理
    void remove(String sessionId);
}
