package be.helmo.planivacances.service;

import be.helmo.planivacances.model.ConfigurationSmtp;
import be.helmo.planivacances.model.User;
import be.helmo.planivacances.model.dto.FormContactDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.ListUsersPage;
import com.google.firebase.auth.UserRecord;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;

@Service
public class UserService {
    private final Set<SseEmitter> emitters = new HashSet<SseEmitter>();
    private final ObjectMapper objectMapper = new ObjectMapper();

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
        List<SseEmitter> emittersToRemove = new ArrayList<>();

        for (SseEmitter emitter : emitters) {
            try {
                int userCount = getNumberOfUsers();
                emitter.send(SseEmitter.event().name("message").data(String.valueOf(userCount),
                        MediaType.TEXT_EVENT_STREAM));
            } catch (Exception e) {
                emitter.complete();
                emittersToRemove.add(emitter);
            }
        }
        emittersToRemove.forEach(emitters::remove);
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

    public boolean deleteUser(String uid) throws FirebaseAuthException {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.deleteUser(uid);
        return true;
    }

    private List<UserRecord> getAdminUsers() throws Exception {
        List<UserRecord> adminUsers = new ArrayList<>();

        ListUsersPage page = FirebaseAuth.getInstance().listUsers(null);
        while (page != null) {
            for (UserRecord user : page.iterateAll()) {
                if (user.getCustomClaims() != null && user.getCustomClaims().containsKey("admin")
                        && (boolean) user.getCustomClaims().get("admin")) {
                    adminUsers.add(user);
                }
            }
            page = page.hasNextPage() ? page.getNextPage() : null;
        }

        return adminUsers;
    }

    public void contactAdmin(FormContactDTO form) {
        try {
            Resource resource = new ClassPathResource("smtp.json");
            ConfigurationSmtp configuration = objectMapper.readValue(
                    resource.getInputStream(),
                    ConfigurationSmtp.class
            );

            HtmlEmail email = new HtmlEmail();
            email.setHostName(configuration.getSmtpHost());
            email.setSmtpPort(configuration.getSmtpPort());
            email.setAuthenticator(new DefaultAuthenticator(configuration.getSmtpUsername(), configuration.getSmtpPassword()));
            email.setStartTLSEnabled(true);
            email.setFrom(configuration.getSmtpUsername());
            email.setSubject(form.getSubject());
            email.setMsg(String.format("Message de : %s\n%s",form.getEmail(),form.getMessage()));

            for(UserRecord user : getAdminUsers()) {
                email.addTo(user.getEmail());
            }

            email.send();
            System.out.println("E-mails envoyés avec succès !");
        } catch (EmailException | IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'envoi des e-mails : " + e.getMessage());
        } catch (Exception exception) {
            exception.printStackTrace();
            System.err.println("Erreur : " + exception.getMessage());
        }
    }

    public User getUser(String uid) throws FirebaseAuthException {
        UserRecord userRecord = FirebaseAuth.getInstance().getUser(uid);
        return new User(userRecord.getUid(),userRecord.getEmail(),userRecord.getDisplayName());
    }
}
