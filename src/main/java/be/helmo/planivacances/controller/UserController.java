package be.helmo.planivacances.controller;

import be.helmo.planivacances.model.User;
import be.helmo.planivacances.model.dto.FormContactDTO;
import be.helmo.planivacances.service.FcmService;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import be.helmo.planivacances.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userServices;

    @Autowired
    private FcmService fcmServices;

    @PostMapping("/{token}/{topic}")
    public boolean setUserRegistrationToken(HttpServletRequest request,
                                            @PathVariable("token") String registrationToken,
                                            @PathVariable("topic") String topic) {

        String uid = (String) request.getAttribute("uid");

        try {
            return fcmServices.subscribeToTopic(topic == null ? uid : topic, registrationToken);
        } catch (FirebaseMessagingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de la sauvegarde du token d'enregistrement fcm");
        }
    }

    @GetMapping
    public User getUserDetails(HttpServletRequest request) {
        String uid = (String) request.getAttribute("uid");
        try {
            return userServices.getUser(uid);
        } catch (FirebaseAuthException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors du chargement de l'utilisateur");
        }
    }

    @GetMapping("/country/{givenDate}")
    public Map<String, Integer> getNumberOfUsersPerCountry(@PathVariable("givenDate") String givenDate) {
        try {
            return userServices.getUserCountPerCountry(givenDate);
        } catch (ExecutionException | InterruptedException | ParseException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de la récupération du nombre d'utilisateur par pays");
        }
    }

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
    public boolean deleteSelfUser(HttpServletRequest request) throws ResponseStatusException {

        String uid = (String) request.getAttribute("uid");
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
