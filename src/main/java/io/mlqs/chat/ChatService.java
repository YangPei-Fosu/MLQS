package io.mlqs.chat;


/**
 * 聊天服务接口
 */
public interface ChatService {
    String chat(String sessionId, String context);
    void deleteChat(String sessionId);
}
