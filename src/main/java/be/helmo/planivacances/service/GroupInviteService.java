package be.helmo.planivacances.service;

import be.helmo.planivacances.model.dto.GroupInviteDTO;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class GroupInviteService {

    private static final String GROUP_COLLECTION_NAME = "groups";

    private static final String USER_COLLECTION_NAME = "users";


    public List<GroupInviteDTO> listUserGroupInvites(String uid) throws ExecutionException, InterruptedException {
        Firestore fdb = FirestoreClient.getFirestore();

        List<GroupInviteDTO> groupInvites = new ArrayList<>();

        // Query to get group IDs where "accepted" is false
        ApiFuture<QuerySnapshot> querySnapshot = fdb.collection(USER_COLLECTION_NAME)
                .document(uid)
                .collection(GROUP_COLLECTION_NAME)
                .whereEqualTo("accepted", false)
                .get();

        // Iterate over the documents to get group IDs
        for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
            String gid = document.getId();

            ApiFuture<DocumentSnapshot> groupDocument = fdb.collection("groups")
                    .document(gid)
                    .get();

            DocumentSnapshot groupSnapshot = groupDocument.get();
            if (groupSnapshot.exists()) {
                // Assuming there is a field called "groupName" in the groups collection
                String groupName = groupSnapshot.getString("groupName");
                groupInvites.add(new GroupInviteDTO(gid, groupName));
            }
        }

        return groupInvites;
    }

    public boolean ChangeUserGroupLink(String gid, String uid, boolean state) throws ExecutionException, InterruptedException {
        Firestore fdb = FirestoreClient.getFirestore();

        HashMap<String, Boolean> map = new HashMap<>();
        map.put("accepted", state);
        ApiFuture<WriteResult> result = fdb.collection(USER_COLLECTION_NAME)
                .document(uid)
                .collection(GROUP_COLLECTION_NAME)
                .document(gid).set(map);

        result.get();

        return result.isDone();
    }

    public String deleteGroupInvite(String uid, String gid) {
        Firestore fdb = FirestoreClient.getFirestore();
        fdb.collection(USER_COLLECTION_NAME)
                .document(uid)
                .collection(GROUP_COLLECTION_NAME)
                .document(gid).delete();

        return "\"L'invitation a bien été supprimé\"";
    }
}
