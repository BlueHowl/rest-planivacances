package be.helmo.planivacances.controller;

import be.helmo.planivacances.model.dto.FormContactDTO;
import be.helmo.planivacances.service.AuthService;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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

    @DeleteMapping
    public boolean deleteSelfUser(@RequestHeader("Authorization") String authorizationHeader) throws ResponseStatusException {

        String uid = authServices.verifyToken(authorizationHeader);

        try {

            if (uid != null) {
                if (userServices.deleteUser(uid)) {
                    userServices.sendSSEUpdateToEveryone();
                    return true;
                }
            } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalide");

        } catch (FirebaseAuthException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de la suppression de l'utilisateur");
        }

        return false;
    }
}
