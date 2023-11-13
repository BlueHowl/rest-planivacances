package be.helmo.planivacances.controller;

import be.helmo.planivacances.model.dto.FormContactDTO;
import be.helmo.planivacances.service.AuthService;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import be.helmo.planivacances.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // TODO only for dev to allow cors
public class UserController {
    @Autowired
    private UserService userServices;
    @Autowired
    private AuthService authServices;

    @GetMapping("/number/flux")
    public SseEmitter getNumberOfUsersStream() {
        SseEmitter sseEmitter = userServices.getNumberUsersStream();
        userServices.sendSSEUpdateToSomeone(sseEmitter);
        return sseEmitter;
    }

    @GetMapping("/number")
    public void refreshNumberOfUsersStream() {
        userServices.sendSSEUpdateToEveryone();
    }

    @PostMapping("admin/message")
    public void contactAdmin(@RequestBody FormContactDTO form) {
        userServices.contactAdmin(form);
    }

    @DeleteMapping("/{uid}")
    public boolean deleteUser(@RequestHeader("Authorization") String authorizationHeader,
                              @PathVariable("uid") String uid) throws FirebaseAuthException {
        if(authServices.verifyToken(authorizationHeader) != null) {
            if(userServices.deleteUser(uid)) {
                userServices.sendSSEUpdateToEveryone();
                return true;
            }
        }
        return false;
    }
}
