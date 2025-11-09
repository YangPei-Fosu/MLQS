package io.mlqs.chat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.mlqs.chat.UserRecordService;
import io.mlqs.chat.db.UserRecordEntity;
import io.mlqs.chat.db.UserRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.alibaba.dashscope.utils.JsonUtils.gson;

@Service
public class UserRecordServiceImpl implements UserRecordService {
    @Autowired
    private UserRecordMapper userRecordDao;

    @Override
    public List<String> getSessions(String userId) {
        UserRecordEntity userRecordEntity = userRecordDao.selectById(userId);
        if(userRecordEntity != null){
            Gson gson = new Gson();
            List<String> list = gson.fromJson(userRecordEntity.getSessionIdList(), new TypeToken<List<String>>(){}.getType());
            return list;
        }
        return new ArrayList<>();
    }

    @Override
    public Boolean deleteSession(String userId ,String sid) {
        if (sid == null)
            return false;
        UserRecordEntity userRecordEntity = userRecordDao.selectById(userId);
        if(userRecordEntity != null){
            Gson gson = new Gson();
            List<String> list = gson.fromJson(userRecordEntity.getSessionIdList(), new TypeToken<List<String>>(){}.getType());
            list.remove(sid);
            userRecordEntity.setSessionIdList(gson.toJson(list));
            userRecordDao.updateById(userRecordEntity);
        }
        return true;
    }

    @Override
    public String addSession(String userId, String sid) {
        UserRecordEntity userRecordEntity = userRecordDao.selectById(userId);
        if(userRecordEntity != null) {
            Gson gson = new Gson();
            List<String> list = gson.fromJson(userRecordEntity.getSessionIdList(), new TypeToken<List<String>>() {
            }.getType());
            list.add(sid);
            userRecordEntity.setSessionIdList(gson.toJson(list));
            userRecordDao.updateById(userRecordEntity);
        }
        //那就说明这个用户没有会话记录
        else {
            UserRecordEntity userRecordEntity1 = new UserRecordEntity(userId, sid);
            ArrayList<String> sids = new ArrayList<>();
            sids.add(sid);
            userRecordEntity1.setSessionIdList(gson.toJson(sids));
            userRecordEntity1.setUserId(userId);
            userRecordDao.insert(userRecordEntity1);
        }
        return sid;
    }
}
