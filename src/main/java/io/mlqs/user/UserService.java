package io.mlqs.user;

import io.mlqs.user.db.UserEntity;

/**
 * 用户服务接口
 */
public interface UserService {
    String login(String account, String password);

    /**
     * 注册用户方法，返回UserId,如果注册成功则返回UserId，否则返回null
     * @param account
     * @param password
     * @return UserId
     */
    String register(String account, String password);
    Boolean delete(String userId, String password);
    Boolean update(String userId, String account, String password);
}
