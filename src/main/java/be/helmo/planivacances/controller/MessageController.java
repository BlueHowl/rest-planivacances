package be.helmo.planivacances.controller;

import be.helmo.planivacances.model.dto.GroupMessageDTO;
import be.helmo.planivacances.service.AuthService;
import be.helmo.planivacances.service.GroupService;
import be.helmo.planivacances.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/chat")
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private AuthService authService;
    @Autowired
    private GroupService groupService;

    @MessageMapping("/connect/{groupId}")
    public void handleWebSocketConnect(@DestinationVariable String groupId, SimpMessageHeaderAccessor headerAccessor) {
        String uid = authService.verifyToken(headerAccessor.getFirstNativeHeader("Authorization"));
        String sessionId = headerAccessor.getSessionId();

        if(uid != null && sessionId != null && groupService.isInGroup(uid,groupId)) {
            List<GroupMessageDTO> recentMessages = messageService.getRecentMessages(groupId, 100);
            recentMessages.forEach(message ->
                    messagingTemplate.convertAndSendToUser(sessionId, "/group/messages", message));
        }
    }

    @MessageMapping("/message/{groupId}")
    public void handleMessage(@DestinationVariable String groupId, GroupMessageDTO message,SimpMessageHeaderAccessor headerAccessor) {
        String uid = authService.verifyToken(headerAccessor.getFirstNativeHeader("Authorization"));
        if(uid != null && groupService.isInGroup(uid,groupId)) {
            message.setGroupId(groupId);
            message.setSender(uid);
            messageService.saveMessage(groupId, message);
            messagingTemplate.convertAndSend(String.format("/group/messages/%s", groupId), message);
        }
    }
}
