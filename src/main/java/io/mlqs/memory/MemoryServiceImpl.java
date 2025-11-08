package io.mlqs.memory;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.memory.ChatMemory;
import io.mlqs.memory.db.MemoryCache;
import io.mlqs.memory.db.MemoryEntity;
import io.mlqs.memory.db.MemoryMapper;
import io.mlqs.utils.LogUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@Data
public class MemoryServiceImpl implements MemoryService{
    //Mysql记忆存储Dao
    @Autowired
    private MemoryMapper memoryMapper;
    //Redis缓存
    @Autowired
    private MemoryCache memoryCache;

    //创建记忆，返回的sessionId是一个32位的UUID
    @Override
    public String createMemory() {
        //生成sessionId
        String sessionId = java.util.UUID.randomUUID().toString();
        Memory memory = new Memory(this, sessionId, 20);
        memoryCache.set(sessionId, ChatMessageSerializer.messagesToJson(memory.messages()));
        return sessionId;
    }

    //获取记忆
    @Override
    public ChatMemory getMemory(String sessionId) {
        Memory memory;
        List<ChatMessage> chatMessages = memoryCache.get(sessionId);
        //缓存中不存在则从Mysql中获取
        if (chatMessages == null) {
            MemoryEntity memoryEntity = memoryMapper.selectById(sessionId);
            memory = new Memory(this, sessionId, 20);
            //有可能Mysql的记忆也不存在，则创建一个空的记忆
            chatMessages = ChatMessageDeserializer.messagesFromJson(memoryEntity==null? "[]" : memoryEntity.getMemory());
            for (ChatMessage chatMessage : chatMessages)
                memory.add(chatMessage);
            memoryCache.set(sessionId, ChatMessageSerializer.messagesToJson(chatMessages));
        }else {
            //缓存中存在则从缓存中获取
            memory = new Memory(this,sessionId, 20);
            memory.messages().addAll(chatMessages);
        }
        return memory;
    }

    //更新记忆
    @Override
    public void updateMemory(String sessionId, ChatMemory chatMemory) {
        memoryCache.set(sessionId, ChatMessageSerializer.messagesToJson(chatMemory.messages()));
    }

    //保存记忆
    @Override
    public void saveMemory(String sessionId) {
        List<ChatMessage> chatMessages = memoryCache.get(sessionId);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //如果Mysql中存在则更新，否则插入
        MemoryEntity memoryEntity = memoryMapper.selectById(sessionId);
        if (memoryEntity != null){
            memoryEntity.setMemory(ChatMessageSerializer.messagesToJson(chatMessages));
            memoryMapper.updateById(memoryEntity);
        }else {
            memoryMapper.insert(new MemoryEntity(sessionId, ChatMessageSerializer.messagesToJson(chatMessages), timestamp ,timestamp ,false));
        }
    }

    //清空记忆
    @Override
    public void clearMemory(String sessionId) {
        List<ChatMessage> chatMessages = memoryCache.get(sessionId);
        if(chatMessages != null)
            chatMessages.clear();
        memoryCache.set(sessionId, ChatMessageSerializer.messagesToJson(chatMessages));
        MemoryEntity memoryEntity = memoryMapper.selectById(sessionId);
        if (memoryEntity != null){
            memoryEntity.setMemory(ChatMessageSerializer.messagesToJson(chatMessages));
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

    @Override
    public List<MemoryEntity> getAllValid() {
        return memoryMapper.selectAllValid();
    }

}
