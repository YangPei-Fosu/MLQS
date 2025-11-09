package io.mlqs.chat;


import dev.langchain4j.data.message.ChatMessage;
import org.apache.logging.log4j.message.Message;

import java.util.List;
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
    Map<String,String> chat(String userId ,String sessionId, String context);
    void deleteChat(String userId ,String sessionId);

    /**
     * 获取会话的所有信息
     * @param
     * @return
     */
    List<ChatMessage> getMessages(String sessionId);

    /**
     *获取所有的会话记录
     * @param userId
     * @return 返回所有的会话ID
     */
    List<String> getSessions(String userId);
}
