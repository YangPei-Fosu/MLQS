package io.mlqs.memory.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.sql.Timestamp;


@TableName("bas_memory")
@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 记忆实体类
 * 这个类用于持久化会话记忆到Mysql
 */
public class MemoryEntity {
    @TableId(value = "session_id", type = IdType.ASSIGN_ID)
    private String sessionId;
    @TableField(value = "memory")
    private String memory;
    @TableField(value = "create_time")
    private Timestamp createTime;
    @TableField(value = "update_time")
    private Timestamp updateTime;
    @TableField(value = "delete_flag")
    private boolean deleteFlag;
}
