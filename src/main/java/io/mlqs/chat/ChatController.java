package io.mlqs.chat;

import dev.langchain4j.data.message.ChatMessageSerializer;
import io.mlqs.chat.entity.HttpRespondDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ChatController {
    @Autowired
    private ChatService chatService;

    @PostMapping("/chat")
    public HttpRespondDTO chat(@RequestParam String uid, @RequestParam String sid,@RequestParam String message) {
        if(sid=="") sid = null;
        Map<String, String> result = chatService.chat(uid ,sid, message);
        return HttpRespondDTO.ok().put("sessionId", result.get("sessionId")).put("context", result.get("context"));
    }

    @PostMapping("/chat/delete")
    public HttpRespondDTO delete(@RequestParam String uid ,@RequestParam String sessionId) {
        chatService.deleteChat(uid, sessionId);
        return HttpRespondDTO.ok();
    }

    @GetMapping("/chat/getMessages")
    public HttpRespondDTO getMessages(@RequestParam String sessionId) {
        String chat = ChatMessageSerializer.messagesToJson(chatService.getMessages(sessionId));
        return HttpRespondDTO.ok().put("messages", chat);
    }

    @GetMapping("/chat/getSessions")
    public HttpRespondDTO getSessions(@RequestParam String uid) {
        List<String> sessions = chatService.getSessions(uid);
        return HttpRespondDTO.ok().put("sessions", sessions);
    }
}
