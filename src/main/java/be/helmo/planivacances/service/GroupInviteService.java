package be.helmo.planivacances.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class GroupInviteService {

    private static final String GROUP_COLLECTION_NAME = "groups";

    private static final String USER_COLLECTION_NAME = "users";

    public boolean ChangeUserGroupLink(String gid, String uid, boolean state) {
        Firestore fdb = FirestoreClient.getFirestore();

        HashMap<String, Boolean> map = new HashMap<>();
        map.put("accepted", state);
        ApiFuture<WriteResult> result = fdb.collection(USER_COLLECTION_NAME)
                .document(uid)
                .collection(GROUP_COLLECTION_NAME)
                .document(gid).set(map);

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
