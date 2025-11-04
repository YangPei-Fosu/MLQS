package io.mlqs.memory;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.memory.ChatMemory;
import io.mlqs.memory.db.MemoryCache;
import io.mlqs.memory.db.MemoryEntity;
import io.mlqs.memory.db.MemoryMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Data
@Service
public class MemoryServiceImpl implements MemoryService{
    //Mysql记忆存储Dao
    @Autowired
    private MemoryMapper memoryMapper;
    //Redis缓存
    private MemoryCache memoryCache;

    //获取记忆
    @Override
    public ChatMemory getMemory(String sessionId) {
        ChatMemory value = memoryCache.get(sessionId);
        if (value == null) {
            MemoryEntity memoryEntity = memoryMapper.selectById(sessionId);
            Memory memory = new Memory(sessionId, 20);
            List<ChatMessage> chatMessages = ChatMessageDeserializer.messagesFromJson(memoryEntity.getMemory());
            for (ChatMessage chatMessage : chatMessages)
                memory.add(chatMessage);
            memoryCache.set(sessionId, memory);
        }
        return value;
    }

    //更新记忆
    @Override
    public void updateMemory(String sessionId, ChatMemory chatMemory) {
        memoryCache.set(sessionId, chatMemory);
    }

    //保存记忆
    @Override
    public void saveMemory(String sessionId) {
        ChatMemory chatMemory = memoryCache.get(sessionId);
        String memory = chatMemory==null? " " : chatMemory.messages().toString();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        memoryMapper.insert(new MemoryEntity(sessionId, memory, timestamp ,timestamp ,false));
    }

    //清空记忆
    @Override
    public void clearMemory(String sessionId) {
        ChatMemory chatMemory = memoryCache.get(sessionId);
        if(chatMemory != null)
            chatMemory.messages().clear();
        memoryCache.set(sessionId, chatMemory);
        MemoryEntity memoryEntity = memoryMapper.selectById(sessionId);
        if (memoryEntity != null){
            memoryEntity.setMemory(chatMemory.messages().toString());
            memoryMapper.updateById(memoryEntity);
        }
    }

    //删除记忆
    @Override
    public void deleteMemory(String sessionId) {
        memoryCache.remove(sessionId);
        MemoryEntity memoryEntity = memoryMapper.selectById(sessionId);
        if (memoryEntity != null){
            memoryEntity.setDeleteFlag(true);
            memoryMapper.updateById(memoryEntity);
        }
    }

    //删除缓存
    @Override
    public void deleteMemoryCache(String sessionId) {
        memoryCache.remove(sessionId);
    }

}
