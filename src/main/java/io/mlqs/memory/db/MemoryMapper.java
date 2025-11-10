package io.mlqs.memory.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemoryMapper extends BaseMapper<MemoryEntity> {
    // 1. 根据会话ID查询（查询id、message、时间）
    MemoryEntity selectBySessionId(@Param("sessionId") String sessionId);

    // 2. 查询所有有效数据（未删除）- 按更新时间倒序
    List<MemoryEntity> selectAllValid();

}
