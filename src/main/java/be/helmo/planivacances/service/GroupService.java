package be.helmo.planivacances.service;

import be.helmo.planivacances.model.dto.GroupDTO;
import be.helmo.planivacances.model.firebase.dto.DBGroupDTO;
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

        ApiFuture<WriteResult> result = dr.set(new DBGroupDTO(group));

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

        return document.exists() ? new GroupDTO(document.toObject(DBGroupDTO.class), document.getId()) : null;
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

            GroupDTO group = new GroupDTO(document.toObject(DBGroupDTO.class), document.getId());
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
        ApiFuture<WriteResult> cApiFuture = fdb.collection(GROUP_COLLECTION_NAME)
                                               .document(gid)
                                               .set(new DBGroupDTO(group));

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

        try {
            for (DocumentReference dr : drs) {
                if (dr.getId().equals(gid)) {

                    ApiFuture<DocumentSnapshot> future = dr.get();
                    DocumentSnapshot documentSnapshot = future.get();

                    if (documentSnapshot.exists()) {
                        // Assuming "accepted" is a boolean field in the document
                        Boolean accepted = documentSnapshot.getBoolean("accepted");

                        if (accepted != null && accepted) {
                            return true;
                        }
                    }
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            return false;
        }

        return false;
    }

    public boolean updateGroupUserCount(String groupId, int delta) {
        Firestore fdb = FirestoreClient.getFirestore();
        DocumentReference groupDocRef = fdb.collection("groups").document(groupId);

        try {
            DocumentSnapshot groupSnapshot = groupDocRef.get().get();

            if (groupSnapshot.exists()) {
                Long currentCount = groupSnapshot.getLong("userCount");

                if (currentCount != null) {
                    long newCount = currentCount + delta;

                    // Ensure the count doesn't go below 0
                    if (newCount >= 0) {
                        groupDocRef.update("userCount", newCount);
                        System.out.println("User count updated successfully");
                        return true;
                    } else {
                        // Handle the case of an invalid user count
                        System.err.println("Invalid user count: " + newCount);
                        return false;
                    }
                } else {
                    // Handle the case where the userCount field is not found
                    System.err.println("userCount field not found in the group document");
                    return false;
                }
            } else {
                // Handle the case where the group is not found
                System.err.println("Group not found");
                return false;
            }
        } catch (Exception e) {
            // Handle exceptions
            e.printStackTrace();
            return false;
        }
    }

}
