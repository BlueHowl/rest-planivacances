package be.helmo.planivacances.controller;

import be.helmo.planivacances.model.dto.GroupMessageDTO;
import be.helmo.planivacances.service.AuthService;
import be.helmo.planivacances.service.GroupService;
import be.helmo.planivacances.service.interfaces.MessageListener;
import be.helmo.planivacances.service.MessageService;
import com.pusher.rest.Pusher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@Controller
@DependsOn("messageService")
@RequestMapping("/api/chat")
public class MessageController implements MessageListener {
    @Autowired
    private MessageService messageService;

    @Autowired
    private AuthService authService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private Pusher pusher;

    @PostConstruct
    public void init() {
        messageService.addListener(this);
    }

    @PreDestroy
    public void destroy() {
        messageService.removeListener(this);
    }

    @PostMapping("/")
    public ResponseEntity<String> authenticate(@RequestParam("socket_id") String socketId,
                                               @RequestParam("channel_name") String channelName,
                                               @RequestHeader("Authorization") String authToken) {
        String uid;
        String gid = channelName.replaceAll("private-","");
        if((uid = authService.verifyToken(authToken)) != null) {
            if (groupService.isInGroup(uid, gid)) {
                String auth = pusher.authenticate(socketId,channelName);
                return ResponseEntity.ok(auth);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    }

    @PostMapping("/messages")
    public ResponseEntity<List<GroupMessageDTO>> sendPreviousMessages(@RequestHeader("Authorization") String authToken,
                                                                      @RequestHeader("GID") String gid) {
        String uid;
        if((uid = authService.verifyToken(authToken)) != null) {
            if(groupService.isInGroup(uid,gid)) {
                List<GroupMessageDTO> previousMessages = messageService.getRecentMessages(gid, 100);
                return ResponseEntity.ok(previousMessages);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/message")
    public ResponseEntity<String> handleMessage(@RequestBody GroupMessageDTO message,
                                                @RequestHeader("Authorization") String authToken) {
        String uid;
        if((uid = authService.verifyToken(authToken)) != null) {
            if (groupService.isInGroup(uid, message.getGroupId())) {
                messageService.saveMessage(message);
                return ResponseEntity.ok("Message envoyé avec succès");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Erreur durant l'envoi du message");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erreur durant l'envoi du message");
        }
    }

    @Override
    public void onNewMessage(GroupMessageDTO message) {
        if(message != null) {
            pusher.trigger(String.format("private-%s",message.getGroupId()),"new_messages",message);
        }
    }
}