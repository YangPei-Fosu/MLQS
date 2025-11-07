package io.mlqs.memory;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 实现ChatMemory接口
 * 以此自定义记忆的Id、添加、清空等功能
 * 注意区分Memory类与MemoryEntity类
 * MemoryEntity类为数据库实体类
 * Memory类为会话记忆类，里面的添加、清空等是提供给AI自己处理的
 * MemoryEntity类则是把聊天记录转换到数据库里存储的实体
 */
@Component
@Data
@NoArgsConstructor
public class Memory implements ChatMemory {
    private MemoryService memoryService;
    private String sessionId;
    private ChatMemory delegate;

    //构造函数注入
    public Memory(MemoryService memoryService, String sessionId, int maxMessages) {
        this.memoryService = memoryService;
        this.sessionId = sessionId;
        this.delegate = MessageWindowChatMemory.builder().maxMessages(maxMessages).build();
    }

    public Memory(String sessionId,int maxMessages) {
        this.sessionId = sessionId;
        this.delegate = MessageWindowChatMemory.builder().maxMessages(maxMessages).build();
    }

    @Override
    public Object id() {
        return sessionId;
    }

    @Override
    public void add(ChatMessage chatMessage) {
        delegate.add(chatMessage);
        memoryService.updateMemory(sessionId, this);
    }

    @Override
    public List<ChatMessage> messages() {
        return delegate.messages();
    }

    @Override
    public void clear() {
        delegate.clear();
        memoryService.clearMemory(sessionId);
    }


}
