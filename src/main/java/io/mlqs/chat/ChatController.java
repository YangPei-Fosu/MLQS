package io.mlqs.chat;

import io.mlqs.chat.entity.HttpRespondDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
    @Autowired
    private ChatService chatService;

    @PostMapping("/chat")
    public HttpRespondDTO chat(String sessionId, String userMessage) {
        return HttpRespondDTO.ok().put("answer", chatService.chat(sessionId, userMessage));
    }

    @PostMapping("/chat/delete")
    public HttpRespondDTO delete(String sessionId) {
        chatService.deleteChat(sessionId);
        return HttpRespondDTO.ok();
    }
}
