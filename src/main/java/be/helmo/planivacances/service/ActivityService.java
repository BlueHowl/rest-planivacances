package be.helmo.planivacances.service;

import be.helmo.planivacances.model.Activity;
import be.helmo.planivacances.model.Place;
import be.helmo.planivacances.model.dto.ActivityDTO;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ActivityService {

    private static final String BASE_COLLECTION_NAME = "groups";
    private static final String ACTIVITY_COLLECTION_NAME = "activities";

    @Autowired
    private PlaceService placeService;

    public String exportCalendar(String gid) throws ExecutionException, InterruptedException {
        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Planivacances-{groupName}//iCal4j 3.0.26//FR"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        calendar.getProperties().add(Method.PUBLISH);

        for(Activity a : getGroupActivities(gid)) {
            VEvent event = new VEvent(new DateTime(a.getStartDate()), a.getTitle());
            event.getProperties().add(new Summary(a.getDescription()));
            event.getProperties().add(new Location(a.getPlaceAddress()));

            Dur dur = new Dur(0, 0, 0, a.getDuration());
            event.getProperties().add(new Duration(dur));

            DateTime endDateTime = new DateTime(a.getStartDate().getTime() + a.getDuration());
            event.getProperties().add(new DtEnd(endDateTime));

            calendar.getComponents().add(event);
        }

        return calendar.toString();
    }

    public String createGroupActivity(String gid, ActivityDTO activity) throws ExecutionException, InterruptedException {
        Firestore fdb = FirestoreClient.getFirestore();
        DocumentReference dr = fdb.collection(BASE_COLLECTION_NAME)
                .document(gid)
                .collection(ACTIVITY_COLLECTION_NAME)
                .document();

        ApiFuture<WriteResult> result = dr.set(activity);

        result.get();

        return dr.getId();
    }

    public boolean updateGroupActivity(String gid, String aid, ActivityDTO activity) throws ExecutionException, InterruptedException {
        Firestore fdb = FirestoreClient.getFirestore();
        DocumentReference dr = fdb.collection(BASE_COLLECTION_NAME)
                .document(gid)
                .collection(ACTIVITY_COLLECTION_NAME)
                .document(aid);

        ApiFuture<WriteResult> result = dr.set(activity);

        result.get();

        return result.isDone();
    }

    public Activity getGroupActivity(String gid, String pid) throws ExecutionException, InterruptedException {
        Firestore fdb = FirestoreClient.getFirestore();
        DocumentReference dr = fdb.collection(BASE_COLLECTION_NAME)
                .document(gid)
                .collection(ACTIVITY_COLLECTION_NAME)
                .document(pid);

        ActivityDTO activityDTO = dr.get().get().toObject(ActivityDTO.class);

        Place place = placeService.getPlace(gid, activityDTO.getPlaceId());

        if(place != null) {
            return new Activity(activityDTO.getTitle(),
                    activityDTO.getDescription(),
                    activityDTO.getStartDate(),
                    activityDTO.getDuration(),
                    place);
        }

        return null;
    }

    public List<Activity> getGroupActivities(String gid) throws ExecutionException, InterruptedException {
        Firestore fdb = FirestoreClient.getFirestore();
        Iterable<DocumentReference> dr = fdb.collection(BASE_COLLECTION_NAME)
                .document(gid)
                .collection(ACTIVITY_COLLECTION_NAME)
                .listDocuments();
        Iterator<DocumentReference> it = dr.iterator();

        List<Activity> activityList = new ArrayList<>();

        while(it.hasNext()) {
            DocumentReference tempDR = it.next();
            ApiFuture<DocumentSnapshot> future = tempDR.get();
            DocumentSnapshot document = future.get();

            ActivityDTO activityDTO = document.toObject(ActivityDTO.class);

            Place place = placeService.getPlace(gid, activityDTO.getPlaceId());

            if(place != null) {
                activityList.add(
                        new Activity(activityDTO.getTitle(),
                                activityDTO.getDescription(),
                                activityDTO.getStartDate(),
                                activityDTO.getDuration(),
                                place)
                );
            }
        }

        return activityList;
    }


}
