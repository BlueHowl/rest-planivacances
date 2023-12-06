package be.helmo.planivacances.service;

import be.helmo.planivacances.model.dto.ActivityDTO;
import be.helmo.planivacances.model.dto.PlaceDTO;
import be.helmo.planivacances.model.firebase.dto.DBActivityDTO;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class ActivityService {

    private static final String BASE_COLLECTION_NAME = "groups";
    private static final String ACTIVITY_COLLECTION_NAME = "activities";

    @Autowired
    private PlaceService placeService;

    public String exportCalendar(String gid) throws ExecutionException, InterruptedException {
        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Planivacances//iCal4j 3.0.26//FR"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        calendar.getProperties().add(Method.PUBLISH);

        for(ActivityDTO a : getGroupActivities(gid).values()) {
            DateTime startDateTime = new DateTime(a.getStartDate());

            VEvent event = new VEvent(startDateTime, a.getTitle());

            event.getProperties().add(new Description(a.getDescription()));
            event.getProperties().add(new Location(a.getPlaceAddress()));

            Dur dur = new Dur(0, 0, 0, a.getDuration());
            event.getProperties().add(new Duration(dur));

            DateTime endDateTime = new DateTime(a.getStartDate().getTime() + a.getDuration());

            event.getProperties().add(new DtEnd(endDateTime));

            calendar.getComponents().add(event);
        }

        return calendar.toString();
    }

    public String createGroupActivity(String gid, DBActivityDTO activity) throws ExecutionException, InterruptedException {
        Firestore fdb = FirestoreClient.getFirestore();
        DocumentReference dr = fdb.collection(BASE_COLLECTION_NAME)
                .document(gid)
                .collection(ACTIVITY_COLLECTION_NAME)
                .document();

        ApiFuture<WriteResult> result = dr.set(activity);

        result.get();

        return dr.getId();
    }

    public boolean updateGroupActivity(String gid, String aid, DBActivityDTO activity) throws ExecutionException, InterruptedException {
        Firestore fdb = FirestoreClient.getFirestore();
        DocumentReference dr = fdb.collection(BASE_COLLECTION_NAME)
                .document(gid)
                .collection(ACTIVITY_COLLECTION_NAME)
                .document(aid);

        ApiFuture<WriteResult> result = dr.set(activity);

        result.get();

        return result.isDone();
    }

    public ActivityDTO getGroupActivity(String gid, String pid) throws ExecutionException, InterruptedException {
        Firestore fdb = FirestoreClient.getFirestore();
        DocumentReference dr = fdb.collection(BASE_COLLECTION_NAME)
                .document(gid)
                .collection(ACTIVITY_COLLECTION_NAME)
                .document(pid);

        DBActivityDTO activityDTO = dr.get().get().toObject(DBActivityDTO.class);

        PlaceDTO place = placeService.getPlace(gid, activityDTO.getPlaceId());

        if(place != null) {
            return new ActivityDTO(activityDTO.getTitle(),
                    activityDTO.getDescription(),
                    activityDTO.getStartDate(),
                    activityDTO.getDuration(),
                    place);
        }

        return null;
    }

    public Map<String, ActivityDTO> getGroupActivities(String gid) throws ExecutionException, InterruptedException {
        Firestore fdb = FirestoreClient.getFirestore();
        Iterable<DocumentReference> dr = fdb.collection(BASE_COLLECTION_NAME)
                .document(gid)
                .collection(ACTIVITY_COLLECTION_NAME)
                .listDocuments();
        Iterator<DocumentReference> it = dr.iterator();

        Map<String, ActivityDTO> activityMap = new HashMap<>();

        while(it.hasNext()) {
            DocumentReference tempDR = it.next();
            ApiFuture<DocumentSnapshot> future = tempDR.get();
            DocumentSnapshot document = future.get();

            DBActivityDTO activityDTO = document.toObject(DBActivityDTO.class);

            PlaceDTO place = placeService.getPlace(gid, activityDTO.getPlaceId());

            if(place != null) {
                activityMap.put(document.getId(),
                        new ActivityDTO(activityDTO.getTitle(),
                                activityDTO.getDescription(),
                                activityDTO.getStartDate(),
                                activityDTO.getDuration(),
                                place)
                );
            }
        }

        return activityMap;
    }

    public String deleteActivity(String gid, String aid) {
        Firestore fdb = FirestoreClient.getFirestore();
        fdb.collection(BASE_COLLECTION_NAME)
                .document(gid)
                .collection(ACTIVITY_COLLECTION_NAME)
                .document(aid).delete();
        return String.format("\"L'activité %s a bien été supprimé\"", aid);
    }

}
