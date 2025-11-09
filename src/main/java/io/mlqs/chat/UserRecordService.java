package io.mlqs.chat;


import java.util.List;

/**
 * 用户会话记录服务接口
 */
public interface UserRecordService {
    List<String> getSessions(String userId);
    Boolean deleteSession(String userId, String sid);

    /**
     * 添加一个新的会话记录
     * @param userId
     * @param sid
     * @return 返回新的会话ID
     */
    String addSession(String userId, String sid);
}
