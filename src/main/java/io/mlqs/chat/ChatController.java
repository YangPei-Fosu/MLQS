package io.mlqs.chat;

import io.mlqs.chat.entity.HttpRespondDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ChatController {
    @Autowired
    private ChatService chatService;

    @PostMapping("/chat")
    public HttpRespondDTO chat(@RequestParam String sid,@RequestParam String message) {
        if(sid=="") sid = null;
        Map<String, String> result = chatService.chat(sid, message);
        return HttpRespondDTO.ok().put("sessionId", result.get("sessionId")).put("context", result.get("context"));
    }

    @PostMapping("/chat/delete")
    public HttpRespondDTO delete(String sessionId) {
        chatService.deleteChat(sessionId);
        return HttpRespondDTO.ok();
    }
}
