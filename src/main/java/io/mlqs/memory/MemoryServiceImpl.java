package io.mlqs.memory;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.memory.ChatMemory;
import io.mlqs.memory.db.MemoryCache;
import io.mlqs.memory.db.MemoryEntity;
import io.mlqs.memory.db.MemoryMapper;
import io.mlqs.utils.LogUtils;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@Data
public class MemoryServiceImpl implements MemoryService, SmartLifecycle {
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
            for (ChatMessage chatMessage : chatMessages)
                memory.add(chatMessage);
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

    /**
     * 结束进程时运行
     * 若进城结束时缓存中存在数据，则保存到Mysql中
     */
    private volatile boolean running = true;

    //销毁
    public void destroy() {
        try {
            int size = memoryCache.size();
            LogUtils.log(MemoryServiceImpl.class, "正在存储全部缓存信息。共" + size + "条数据");
            LogUtils.Bar progress = LogUtils.progress(MemoryServiceImpl.class, "正在存储全部缓存信息。", 0, size);
            progress.start();
            for (String s : memoryCache.Iterator()) {
                saveMemory(s);
                deleteMemoryCache(s);
                progress.update(1);
            }
            LogUtils.log(MemoryServiceImpl.class, "存储完成。");
        }catch (Exception e){
            LogUtils.log(MemoryServiceImpl.class, "存储失败。");
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
