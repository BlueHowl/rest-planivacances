package be.helmo.planivacances.service;

import be.helmo.planivacances.model.dto.GroupMessageDTO;
import be.helmo.planivacances.service.interfaces.MessageListener;
import com.google.firebase.database.*;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@DependsOn("firebaseInitialization")
public class MessageService {
    private Set<MessageListener> messageListeners = new HashSet<>();

    @PostConstruct
    public void init() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://planivacances-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        DatabaseReference groupMessages = databaseReference.child("group-messages");

        groupMessages.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                DatabaseReference childReference = groupMessages.child(snapshot.getKey());
                childReference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                        GroupMessageDTO message = snapshot.getValue(GroupMessageDTO.class);
                        if(message != null) {
                            for(MessageListener messageListener : messageListeners) {
                                messageListener.onNewMessage(message);
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot snapshot, String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot snapshot, String previousChildName) {

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Erreur lors de la lecture des données : " + error.getMessage());
            }
        });
    }

    public List<GroupMessageDTO> getRecentMessages(String groupId, int nbrMessages) {
        CompletableFuture<List<GroupMessageDTO>> future = new CompletableFuture<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://planivacances-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        DatabaseReference groupMessagesRef = databaseReference.child("group-messages").child(groupId);

        groupMessagesRef.limitToLast(nbrMessages).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<GroupMessageDTO> messages = new ArrayList<>();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    GroupMessageDTO message = messageSnapshot.getValue(GroupMessageDTO.class);
                    if (message != null) {
                        messages.add(message);
                    }
                }
                future.complete(messages);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Erreur lors de la récupération des messages : " + databaseError.getMessage());
                future.completeExceptionally(new RuntimeException("Erreur lors de la récupération des messages"));
            }
        });

        try {
            return future.join();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void saveMessage(GroupMessageDTO message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://planivacances-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        DatabaseReference groupMessagesRef = databaseReference.child("group-messages").child(message.getGroupId());
        groupMessagesRef.push().setValueAsync(message);
    }

    public void addListener(MessageListener messageListener) {
        messageListeners.add(messageListener);
    }

    public void removeListener(MessageListener messageListener) {
        messageListeners.remove(messageListener);
    }
}
