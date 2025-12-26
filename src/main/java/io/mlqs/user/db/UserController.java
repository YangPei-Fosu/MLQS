package io.mlqs.user.db;

import io.mlqs.chat.entity.HttpRespondDTO;
import io.mlqs.user.UserService;
import io.mlqs.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public HttpRespondDTO login(@RequestBody Map<String,String> request) {
        //起始时间
        long start = System.currentTimeMillis();

        String uid = userService.login(request.get("account"), request.get("password"));

        LogUtils.log(UserController.class, "用户登录耗时："+ (System.currentTimeMillis() - start) + "ms");

        if (uid!=null) return HttpRespondDTO.ok().put("uid", uid);
        else return HttpRespondDTO.error();
    }

    @PostMapping("/register")
    public HttpRespondDTO register(@RequestBody Map<String,String> request) {
        String uid = userService.register(request.get("account"), request.get("password"));
        if(uid==null)
            return HttpRespondDTO.error().put("message","账号已存在");
        return HttpRespondDTO.ok().put("uid", uid);
    }

    @PostMapping("/delete")
    public HttpRespondDTO delete(@RequestBody Map<String,String> request) {
        Boolean result = userService.delete(request.get("account"), request.get("password"));
        return HttpRespondDTO.ok().put("result", result);
    }

    @PostMapping("/update")
    public HttpRespondDTO update(@RequestBody Map<String,String> request) {
        Boolean result = userService.update(request.get("uid"),request.get("account"), request.get("password"));
        return HttpRespondDTO.ok().put("result", result);
    }
}
