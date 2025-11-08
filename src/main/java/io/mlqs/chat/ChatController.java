package io.mlqs.chat;

import io.mlqs.chat.entity.HttpRespondDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class ChatController {
    @Autowired

    private ChatService chatService;
    @PostMapping("/chat")
    public HttpRespondDTO chat(@RequestBody Map<String, String> request) {
        String sessionId = request.get("sessionId");
        String userMessage = request.get("userMessage");
        if(Objects.equals(sessionId, "")) sessionId = null;
        Map<String, String> result = chatService.chat(sessionId, userMessage);
        return HttpRespondDTO.ok().put("sessionId", result.get("sessionId")).put("context", result.get("context"));
    }

    @PostMapping("/chat/delete")
    public HttpRespondDTO delete(@RequestBody Map<String, String> request) {
        String sessionId = request.get("sessionId");
        chatService.deleteChat(sessionId);
        return HttpRespondDTO.ok();
    }
}
