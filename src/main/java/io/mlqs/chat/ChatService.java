package io.mlqs.chat;


import java.util.Map;

/**
 * 聊天服务接口
 */
public interface ChatService {
    /**
     * 交互接口，如果传入的sessionId为空，则相当于创建新的会话
     * @param sessionId
     * @param context
     * @return 有两个键值对【sessionId, sessionId的值】【context, Ai的回复】
     */
    Map<String,String> chat(String sessionId, String context);
    void deleteChat(String sessionId);
}
