package io.mlqs.user.db;

import io.mlqs.chat.entity.HttpRespondDTO;
import io.mlqs.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public HttpRespondDTO login(@RequestParam String account, @RequestParam String password) {
        String uid = userService.login(account, password);
        return HttpRespondDTO.ok().put("uid", uid);
    }

    @PostMapping("/register")
    public HttpRespondDTO register(@RequestParam String account, @RequestParam String password) {
        String uid = userService.register(account, password);
        return HttpRespondDTO.ok().put("uid", uid);
    }

    @PostMapping("/delete")
    public HttpRespondDTO delete(@RequestParam String account, @RequestParam String password) {
        Boolean result = userService.delete(account, password);
        return HttpRespondDTO.ok().put("result", result);
    }

    @PostMapping("/update")
    public HttpRespondDTO update(@RequestParam String uid,@RequestParam String account, @RequestParam String password) {
        Boolean result = userService.update(uid,account, password);
        return HttpRespondDTO.ok().put("result", result);
    }
}
