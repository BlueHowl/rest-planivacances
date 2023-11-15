package be.helmo.planivacances.service;

import be.helmo.planivacances.model.Group;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class GroupService {

    private static final String COLLECTION_NAME = "groups";

    public String createGroup(Group group) throws ExecutionException, InterruptedException {
        Firestore fdb = FirestoreClient.getFirestore();
        DocumentReference dr = fdb.collection(COLLECTION_NAME).document();

        ApiFuture<WriteResult> result = dr.set(group);

        // Block until the document is written (optional)
        result.get();

        return dr.getId();
    }

    public Group getGroup(String gid) throws ExecutionException, InterruptedException {
        Firestore fdb = FirestoreClient.getFirestore();
        DocumentReference dr = fdb.collection(COLLECTION_NAME).document(gid);
        ApiFuture<DocumentSnapshot> future = dr.get();

        DocumentSnapshot document = future.get();

        return document.exists() ? document.toObject(Group.class) : null;
    }

    public List<Group> getGroups() throws ExecutionException, InterruptedException {
        Firestore fdb = FirestoreClient.getFirestore();
        Iterable<DocumentReference> dr = fdb.collection(COLLECTION_NAME).listDocuments();
        Iterator<DocumentReference> it = dr.iterator();

        List<Group> groupList = new ArrayList<>();

        while(it.hasNext()) {
            DocumentReference tempDR = it.next();
            ApiFuture<DocumentSnapshot> future = tempDR.get();
            DocumentSnapshot document = future.get();

            groupList.add(document.toObject(Group.class));
        }

        return groupList;
    }

    public String updateGroup(String gid, Group group) throws ExecutionException, InterruptedException {
        Firestore fdb = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> cApiFuture = fdb.collection(COLLECTION_NAME).document(gid).set(group);

        return String.format("\"Groupe modifié le %s\"", cApiFuture.get().getUpdateTime().toDate()); //todo formattage manuel autorisé?
    }

    public String deleteGroup(String gid) {
        Firestore fdb = FirestoreClient.getFirestore();
        fdb.collection(COLLECTION_NAME).document(gid).delete();
        return String.format("\"Le groupe %s a bien été supprimé\"", gid);
    }

    public boolean isInGroup(String uid, String gid) {
        Firestore fdb = FirestoreClient.getFirestore();
        DocumentReference userRef = fdb.collection("users").document(uid);

        try {
            DocumentSnapshot document = userRef.get().get();

            if (document.exists()) {
                if (document.contains("groups")) {
                    List<String> groups = (List<String>) document.get("groups");
                    if (groups != null && groups.contains(gid)) {
                        return true;
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
        return false;
    }

}
