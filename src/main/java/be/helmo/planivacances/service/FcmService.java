package be.helmo.planivacances.service;

import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class FcmService {

    public boolean subscribeUser(String uid, String registrationToken) throws FirebaseMessagingException {
        TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopic(
                Collections.singletonList(registrationToken), uid);

        return response.getSuccessCount() == 1;
    }

    public void sendInviteNotification(String uid, String groupName) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle("Planivacances-invitation")
                        .setBody(String.format("Vous avez été invité à rejoindre le groupe %s", groupName))
                        .build())
                .setTopic(uid)
                .build();

        // Send a message to the devices subscribed to the provided topic.
        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println(response);
    }

}
