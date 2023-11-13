package be.helmo.planivacances.service;

import be.helmo.planivacances.model.Activity;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ActivityService {

    private static final String BASE_COLLECTION_NAME = "groups";
    private static final String ACTIVITY_COLLECTION_NAME = "activities";

    public Calendar exportCalendar(String gid) throws ExecutionException, InterruptedException {
        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Planivacances-{groupName}//iCal4j 3.0.26//FR"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        calendar.getProperties().add(Method.PUBLISH);

        for(Activity a : getGroupActivities(gid)) {
            VEvent event = new VEvent(new DateTime(a.getStartDate()), String.format("%s\n%s", a.getTitle(), a.getDescription()));
            event.getProperties().add(new Location(a.getPlaceAddress()));
            event.getDuration().setDuration(Duration.ofMillis(a.getMsDuration()));
            calendar.getComponents().add(event);
        }

        System.out.println(calendar);

        return calendar;
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

            activityList.add(document.toObject(Activity.class));
        }

        return activityList;
    }


}
