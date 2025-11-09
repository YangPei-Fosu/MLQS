package io.mlqs.chat.db;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户会话记录表
 */
@TableName("bas_users_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRecordEntity {
    @TableId(value = "user_id")
    private String userId;

    @TableField(value = "session_id_list")
    private String sessionIdList;
}
