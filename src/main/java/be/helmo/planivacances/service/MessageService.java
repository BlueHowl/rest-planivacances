package be.helmo.planivacances.service;

import be.helmo.planivacances.model.dto.GroupMessageDTO;
import com.google.firebase.database.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    public List<GroupMessageDTO> getRecentMessages(String groupId, int nbrMessages) {
        List<GroupMessageDTO> messages = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference groupMessagesRef = databaseReference.child("group-messages").child(groupId);

        groupMessagesRef.limitToLast(nbrMessages).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    GroupMessageDTO message = messageSnapshot.getValue(GroupMessageDTO.class);
                    if (message != null) {
                        messages.add(message);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Erreur lors de la récupération des messages : " + databaseError.getMessage());
            }
        });

        return messages;
    }

    public void saveMessage(String groupId, GroupMessageDTO message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference groupMessagesRef = databaseReference.child("group-messages").child(groupId);
        groupMessagesRef.push().setValueAsync(message);
    }
}
