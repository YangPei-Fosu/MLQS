package io.mlqs.user;

import io.mlqs.user.db.UserEntity;
import io.mlqs.user.db.UserMapper;
import io.mlqs.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserServiceImpl implements UserService, SmartLifecycle {
    @Autowired
    private UserMapper userMapper;

    //redis缓存,[用户账户名，用户对象]
    @Autowired
    private RedisTemplate<String, Object> rt;
    //缓存用户对象过期时间1天
    private Long timeout = 24 * 60 * 60 * 1000L;

    @Override
    public String login(String account, String password) {
        LogUtils.log(UserServiceImpl.class, "缓存有" + rt.keys("*").size() + "条缓存数据。");

        Map<String,Object> map = new HashMap<>();
        map.put("account",account);
        map.put("password",password);
        if(rt.hasKey(account)){
            UserEntity userEntity = (UserEntity) rt.opsForValue().get(account);
            //刷新过期时间
            rt.expire(account, timeout, TimeUnit.MILLISECONDS);
            return userEntity.getUserId();
        }
        else{
            List<UserEntity> userEntityList = userMapper.selectByMap(map);
            if (userEntityList.isEmpty()){
                return null;
            }
            //缓存用户对象
            rt.opsForValue().set(userEntityList.get(0).getAccount(), userEntityList.get(0) ,timeout, TimeUnit.MILLISECONDS);
            return userEntityList.get(0).getUserId();
        }
    }

    @Override
    public String register(String account, String password) {
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
        //缓存用户对象
        rt.opsForValue().set(account, userEntity ,timeout, TimeUnit.MILLISECONDS);
        return userId;
    }

    @Override
    public Boolean delete(String account, String password) {
        Map<String,Object> map = new HashMap<>();
        map.put("account",account);
        map.put("password",password);
        UserEntity user = null;
        //如果在缓冲中就先删除缓存数据
        if(rt.hasKey(account)){
            user = (UserEntity) rt.opsForValue().get(account);
            rt.delete(account);
        }
        //如果缓存中存在该用户，再尝试去删除数据库数据
        if(user != null){
            return userMapper.deleteById(user) > 0;
        }
        //否则直接尝试去删除数据库数据
        else{
            return userMapper.deleteById(account) > 0;
        }
    }

    @Override
    public Boolean update(String userId, String account, String password) {
        UserEntity userEntity = null;
        userEntity = (UserEntity) rt.opsForValue().get(account);
        if(userEntity != null){
            userEntity.setAccount(account);
            userEntity.setPassword(password);
            //更新缓存
            rt.opsForValue().set(userEntity.getAccount(), userEntity ,timeout, TimeUnit.MILLISECONDS);
            return userMapper.updateById(userEntity) > 0;
        }
        else{
            userEntity = userMapper.selectById(userId);
            if(userEntity != null){
                userEntity.setAccount(account);
                userEntity.setPassword(password);
                rt.opsForValue().set(userEntity.getAccount(), userEntity ,timeout, TimeUnit.MILLISECONDS);
                return userMapper.updateById(userEntity) > 0;
            }
        }
        return false;
    }


    /**
     * 退出进程时销毁用户的缓存数据
     */
    private volatile boolean running = true;

    @Override
    public void start() {
        running = true;
    }

    @Override
    public void stop() {
        LogUtils.log(UserServiceImpl.class, "正在清理用户缓存数据。");
        rt.delete(rt.keys("*"));
        LogUtils.log(UserServiceImpl.class, "销毁完成。");
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
