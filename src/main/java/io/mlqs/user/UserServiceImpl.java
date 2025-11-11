package io.mlqs.user;

import io.mlqs.user.db.UserEntity;
import io.mlqs.user.db.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserMapper userMapper;

    @Override
    public String login(String account, String password) {
        Map<String,Object> map = new HashMap<>();
        map.put("account",account);
        map.put("password",password);
        List<UserEntity> userEntityList = userMapper.selectByMap(map);
        if (userEntityList.isEmpty()){
            return null;
        }
        return userEntityList.get(0).getUserId();
    }

    @Override
    public String register(String account, String password) {
        if (userMapper.selectById(account) != null){
            return null;
        }
        String userId = UUID.randomUUID().toString();
        UserEntity userEntity = new UserEntity();
        userEntity.setAccount(account);
        userEntity.setPassword(password);
        userEntity.setCreateTime(new Timestamp(System.currentTimeMillis()));
        userEntity.setUserId(userId);
        userMapper.insert(userEntity);
        return userId;
    }

    @Override
    public Boolean delete(String userId, String password) {
        UserEntity userEntity = userMapper.selectById(userId);
        if (userEntity != null && userEntity.getPassword().equals(password))
            return userMapper.deleteById(userId) > 0;
        return false;
    }

    @Override
    public Boolean update(String userId, String account, String password) {
        UserEntity userEntity = userMapper.selectById(userId);
        if(userEntity != null){
            userEntity.setAccount(account);
            userEntity.setPassword(password);
            return userMapper.updateById(userEntity) > 0;
        }
        return false;
    }
}
