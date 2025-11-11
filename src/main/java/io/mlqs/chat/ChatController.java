package io.mlqs.chat;

import dev.langchain4j.data.message.ChatMessageSerializer;
import io.mlqs.chat.entity.HttpRespondDTO;
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
    public HttpRespondDTO chat(@RequestBody Map<String,String> request) {
        String uid = request.get("user_id");
        String sid = request.get("session_id");
        String message = request.get("userMessage");
        if(Objects.equals(sid, "")) sid = null;
        Map<String, String> result = chatService.chat(uid ,sid, message);
        return HttpRespondDTO.ok().put("sessionId", result.get("sessionId")).put("context", result.get("context"));
    }

    @PostMapping("/chat/delete")
    public HttpRespondDTO delete(@RequestBody Map<String,String> request) {
        String uid = request.get("user_id");
        String sessionId = request.get("session_id");
        chatService.deleteChat(uid, sessionId);
        return HttpRespondDTO.ok();
    }

    @PostMapping("/chat/getMessages")
    public HttpRespondDTO getMessages(@RequestBody Map<String,String> request) {
        String sessionId = request.get("session_id");
        String chat = ChatMessageSerializer.messagesToJson(chatService.getMessages(sessionId));
        return HttpRespondDTO.ok().put("messages", chat);
    }

    @PostMapping("/chat/getSessions")
    public HttpRespondDTO getSessions(@RequestBody Map<String,String> request) {
        String uid = request.get("user_id");
        List<String> sessions = chatService.getSessions(uid);
        return HttpRespondDTO.ok().put("sessions", sessions);
    }
}
