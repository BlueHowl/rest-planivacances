package be.helmo.planivacances.service;

import be.helmo.planivacances.model.dto.GroupDTO;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class GroupService {

    private static final String GROUP_COLLECTION_NAME = "groups";

    private static final String USER_COLLECTION_NAME = "users";

    @Autowired
    private GroupInviteService groupInviteServices;

    public String createGroup(GroupDTO group) throws ExecutionException, InterruptedException {
        Firestore fdb = FirestoreClient.getFirestore();
        DocumentReference dr = fdb.collection(GROUP_COLLECTION_NAME).document();

        ApiFuture<WriteResult> result = dr.set(group);

        result.get();

        String gid = dr.getId();

        //create gid link to uid
        groupInviteServices.ChangeUserGroupLink(gid, group.getOwner(), true);

        return gid;
    }

    public GroupDTO getGroup(String gid) throws ExecutionException, InterruptedException {
        Firestore fdb = FirestoreClient.getFirestore();
        DocumentReference dr = fdb.collection(GROUP_COLLECTION_NAME).document(gid);
        ApiFuture<DocumentSnapshot> future = dr.get();

        DocumentSnapshot document = future.get();

        return document.exists() ? document.toObject(GroupDTO.class) : null;
    }

    public List<GroupDTO> getGroups(String uid) throws ExecutionException, InterruptedException {
        Firestore fdb = FirestoreClient.getFirestore();
        Iterable<DocumentReference> drs = fdb.collection(GROUP_COLLECTION_NAME).listDocuments();
        Iterator<DocumentReference> it = drs.iterator();

        List<GroupDTO> groupList = new ArrayList<>();

        while(it.hasNext()) {
            DocumentReference tempDR = it.next();
            ApiFuture<DocumentSnapshot> future = tempDR.get();
            DocumentSnapshot document = future.get();

            GroupDTO group = document.toObject(GroupDTO.class);
            String gid = document.getId();

            if(document.exists() && isInGroup(uid, gid)) {
                group.setGid(gid);
                groupList.add(group);
            }
        }

        return groupList;
    }

    public String updateGroup(String gid, GroupDTO group) throws ExecutionException, InterruptedException {
        Firestore fdb = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> cApiFuture = fdb.collection(GROUP_COLLECTION_NAME).document(gid).set(group);

        return String.format("\"Groupe modifié le %s\"", cApiFuture.get().getUpdateTime().toDate()); //todo formattage manuel autorisé?
    }

    public String deleteGroup(String gid) {
        Firestore fdb = FirestoreClient.getFirestore();
        fdb.collection(GROUP_COLLECTION_NAME).document(gid).delete();
        return String.format("\"Le groupe %s a bien été supprimé\"", gid);
    }

    public boolean isInGroup(String uid, String gid) {
        Firestore fdb = FirestoreClient.getFirestore();
        Iterable<DocumentReference> drs = fdb.collection(USER_COLLECTION_NAME)
                .document(uid)
                .collection(GROUP_COLLECTION_NAME).listDocuments();

        Iterator<DocumentReference> it = drs.iterator();

        while(it.hasNext()) {
            if(it.next().getId().equals(gid)) {
                return true;
            }
        }

        return false;
    }

}
