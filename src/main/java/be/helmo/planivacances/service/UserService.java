package be.helmo.planivacances.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.ListUsersPage;
import com.google.firebase.auth.UserRecord;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Service
public class UserService {
    private final Set<SseEmitter> emitters = new HashSet<SseEmitter>();

    /**
     * Récupère le nombre d'utilisateurs de PlaniVacances
     * 
     * @return (int) Nombre d'utilisateurs de PlaniVacances
     * @throws FirebaseAuthException
     */
    private int getNumberOfUsers() throws FirebaseAuthException {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        ListUsersPage userPage = auth.listUsers(null);

        int userCount = 0;
        while (userPage != null) {
            for (UserRecord user : userPage.getValues()) {
                userCount++;
            }
            userPage = userPage.getNextPage();
        }

        return userCount;
    }

    public SseEmitter getNumberUsersStream() {
        SseEmitter newEmitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(newEmitter);
        return newEmitter;
    }

    public void sendSSEUpdateToEveryone() {
        Iterator<SseEmitter> iterator = emitters.iterator();
        while (iterator.hasNext()) {
            SseEmitter emitter = iterator.next();
            try {
                int userCount = getNumberOfUsers();
                emitter.send(SseEmitter.event().name("message").data(String.valueOf(userCount),
                        MediaType.TEXT_EVENT_STREAM));
            } catch (Exception e) {
                emitter.complete();
                iterator.remove();
            }
        }
    }

    public void sendSSEUpdateToSomeone(SseEmitter emitter) {
        if(emitter != null) {
            try {
                int userCount = getNumberOfUsers();
                emitter.send(SseEmitter.event().name("message").data(String.valueOf(userCount),
                        MediaType.TEXT_EVENT_STREAM));
            } catch (Exception e) {
                emitter.complete();
            }
        }
    }
}
