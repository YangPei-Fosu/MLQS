package io.mlqs.user.db;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@TableName("bas_users")
@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 用户表
 */
public class UserEntity {
    @TableId(value = "user_id")
    private String userId;
    @TableField(value = "password")
    private String password;
    @TableField(value = "account")
    private String account;
    @TableField(value = "create_time")
    private Timestamp createTime;


}
