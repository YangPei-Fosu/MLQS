package io.mlqs.user;

import io.mlqs.user.db.UserEntity;
import io.mlqs.user.db.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@Slf4j
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
        try {
            userMapper.insert(userEntity);
        }
        catch (DuplicateKeyException e) {
            return null;
        }
        return userId;
    }

    @Override
    public Boolean delete(String account, String password) {
        Map<String,Object> map = new HashMap<>();
        map.put("account",account);
        map.put("password",password);
        List<UserEntity> userEntityList = userMapper.selectByMap(map);
        System.out.println(userEntityList.get(0));
        if (userEntityList.isEmpty()) return false;
        if (userEntityList.get(0).getPassword().equals(password))
            return userMapper.deleteById(userEntityList.get(0).getUserId()) > 0;
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
