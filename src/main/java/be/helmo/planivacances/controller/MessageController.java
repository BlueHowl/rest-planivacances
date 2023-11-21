package be.helmo.planivacances.controller;

import be.helmo.planivacances.model.dto.GroupMessageDTO;
import be.helmo.planivacances.service.AuthService;
import be.helmo.planivacances.service.GroupService;
import be.helmo.planivacances.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private AuthService authService;
    @Autowired
    private GroupService groupService;

    private Map<String,List<String>> groups = new HashMap<>();

    private void addUserInGroup(String gid,String username) {
        groups.computeIfAbsent(gid,k -> new ArrayList<>()).add(username);
    }

    private List<String> getUsersForGid(String gid) {
        return groups.getOrDefault(gid, new ArrayList<>());
    }

    private void removeEntry(String gid, String username) {
        List<String> usernames = groups.get(gid);
        if (usernames != null) {
            usernames.remove(username);
            if (usernames.isEmpty()) {
                groups.remove(gid);
            }
        }
    }

    @EventListener
    public void handleWebSocketSubscribe(SessionSubscribeEvent event) throws ExecutionException, InterruptedException {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();

        if ("/user/group/messages".equals(destination)) {
            String token = headerAccessor.getFirstNativeHeader("Authorization");
            String groupId = headerAccessor.getFirstNativeHeader("GroupId");
            String uid = authService.verifyToken(token);
            String username = headerAccessor.getUser().getName();

            if(uid != null && username != null && groupService.isInGroup(uid,groupId)) {
                addUserInGroup(groupId,username);
                List<GroupMessageDTO> recentMessages = messageService.getRecentMessages(groupId, 100);
                recentMessages.forEach(message ->
                    messagingTemplate.convertAndSendToUser(username,"/group/messages",message)
                );
            }
        }
    }

    @EventListener
    public void handleWebSocketClose(SessionUnsubscribeEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());

        String token = headerAccessor.getFirstNativeHeader("Authorization");
        String groupId = headerAccessor.getFirstNativeHeader("GroupId");
        String uid = authService.verifyToken(token);
        String username = headerAccessor.getUser().getName();

        if(uid != null && username != null) {
            removeEntry(groupId,username);
        }
    }

    @MessageMapping("/message")
    public void handleMessage(@Payload  GroupMessageDTO message, SimpMessageHeaderAccessor headerAccessor) throws ExecutionException, InterruptedException {
        String uid = authService.verifyToken(headerAccessor.getFirstNativeHeader("Authorization"));
        String groupId = headerAccessor.getFirstNativeHeader("GroupId");
        if(uid != null && groupId != null && groupService.isInGroup(uid,groupId)) {
            message.setGroupId(groupId);
            message.setSender(uid);
            messageService.saveMessage(groupId, message);
            for(var username : getUsersForGid(groupId)) {
                messagingTemplate.convertAndSendToUser(username,"/group/messages",message);
            }
        }
    }
}
