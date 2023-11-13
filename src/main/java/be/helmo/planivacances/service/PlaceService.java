package be.helmo.planivacances.service;

import be.helmo.planivacances.model.Place;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class PlaceService {

    private static final String BASE_COLLECTION_NAME = "groups";
    private static final String PLACE_COLLECTION_NAME = "places";

    public String createPlace(String gid, Place place) throws ExecutionException, InterruptedException {
        Firestore fdb = FirestoreClient.getFirestore();

        DocumentReference dr = fdb.collection(BASE_COLLECTION_NAME)
                .document(gid)
                .collection(PLACE_COLLECTION_NAME)
                .document();

        ApiFuture<WriteResult> result = dr.set(place);

        // Block until the document is written (optional)
        result.get();

        return dr.getId();
    }

    public Place getPlace(String gid, String pid) throws ExecutionException, InterruptedException {
        Firestore fdb = FirestoreClient.getFirestore();

        DocumentReference dr = fdb.collection(BASE_COLLECTION_NAME)
                .document(gid)
                .collection(PLACE_COLLECTION_NAME)
                .document(pid);
        ApiFuture<DocumentSnapshot> future = dr.get();

        DocumentSnapshot document = future.get();

        return document.exists() ? document.toObject(Place.class) : null;
    }

    public List<Place> getGroupPlaces(String gid) throws ExecutionException, InterruptedException {
        Firestore fdb = FirestoreClient.getFirestore();

        Iterable<DocumentReference> dr = fdb.collection(BASE_COLLECTION_NAME)
                .document(gid)
                .collection(PLACE_COLLECTION_NAME)
                .listDocuments();

        Iterator<DocumentReference> it = dr.iterator();

        List<Place> groupList = new ArrayList<>();

        while(it.hasNext()) {
            DocumentReference tempDR = it.next();
            ApiFuture<DocumentSnapshot> future = tempDR.get();
            DocumentSnapshot document = future.get();

            groupList.add(document.toObject(Place.class));
        }

        return groupList;
    }

    public String deletePlace(String gid, String pid) {
        Firestore fdb = FirestoreClient.getFirestore();

        ApiFuture<WriteResult> cApiFuture = fdb.collection(BASE_COLLECTION_NAME)
                .document(gid)
                .collection(PLACE_COLLECTION_NAME)
                .document(pid).delete();
        return "\"Le lieu à bien été supprimé\"";
    }
}
