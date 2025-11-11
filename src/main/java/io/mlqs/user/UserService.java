package io.mlqs.user;

import io.mlqs.user.db.UserEntity;

/**
 * 用户服务接口
 */
public interface UserService {
    String login(String account, String password);

    /**
     * 注册用户方法，返回UserId
     * @param account
     * @param password
     * @return UserId
     */
    String register(String account, String password);
    Boolean delete(String userId, String password);
    Boolean update(String userId, String account, String password);
}
