package be.helmo.planivacances.service;

import be.helmo.planivacances.model.dto.GroupMessageDTO;
import com.google.firebase.database.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class MessageService {
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

    public void saveMessage(String groupId, GroupMessageDTO message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://planivacances-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        DatabaseReference groupMessagesRef = databaseReference.child("group-messages").child(groupId);
        groupMessagesRef.push().setValueAsync(message);
    }
}
