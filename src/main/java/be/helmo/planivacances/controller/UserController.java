package be.helmo.planivacances.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import be.helmo.planivacances.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // TODO only for dev to allow cors
public class UserController {
    @Autowired
    private UserService userServices;

    @GetMapping("/number")
    public SseEmitter getNumberOfUsersStream() {
        SseEmitter sseEmitter = userServices.getNumberUsersStream();
        userServices.sendSSEUpdate();
        return sseEmitter;
    }
}
