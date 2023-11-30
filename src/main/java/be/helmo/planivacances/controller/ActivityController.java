package be.helmo.planivacances.controller;

import be.helmo.planivacances.model.Activity;
import be.helmo.planivacances.model.dto.ActivityDTO;
import be.helmo.planivacances.model.dto.GroupDTO;
import be.helmo.planivacances.service.ActivityService;
import be.helmo.planivacances.service.FcmService;
import be.helmo.planivacances.service.GroupService;
import be.helmo.planivacances.service.PlaceService;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired
    private GroupService groupServices;

    @Autowired
    private ActivityService activityServices;

    @Autowired
    private PlaceService placeServices;

    @Autowired
    private FcmService fcmServices;

    @PostMapping("/{gid}")
    public String createActivity(@PathVariable("gid") String gid, @RequestBody Activity activity) {

        try {
            String pid = placeServices.createOrGetPlace(gid, activity.getPlace());

            ActivityDTO activityDTO = new ActivityDTO(
                    activity.getTitle(),
                    activity.getDescription(),
                    activity.getStartDate(),
                    activity.getDuration(),
                    pid);

            GroupDTO group = groupServices.getGroup(gid);

            fcmServices.sendActivityAddedNotification(gid, group.getGroupName(), activity.getTitle());

            return activityServices.createGroupActivity(gid, activityDTO);
        } catch (ExecutionException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de la création de l'activité");
        } catch (FirebaseMessagingException e) {
            System.out.printf("Notification de l'activité %s non distribuée pour le groupe %s", activity.getTitle(), gid);
            return null;
        }
    }

    @PutMapping("/{gid}/{aid}")
    public boolean updateGroupActivity(@PathVariable("gid") String gid,
                                      @PathVariable("aid") String aid,
                                      @RequestBody Activity activity) {

        try {
            String pid = placeServices.createOrGetPlace(gid, activity.getPlace());

            ActivityDTO activityDTO = new ActivityDTO(
                    activity.getTitle(),
                    activity.getDescription(),
                    activity.getStartDate(),
                    activity.getDuration(),
                    pid);

            return activityServices.updateGroupActivity(gid, aid, activityDTO);
        } catch (ExecutionException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de la mise à jour de l'activité");
        }
    }

    @GetMapping("/{gid}/{aid}")
    public Activity getGroupActivity(@PathVariable("gid") String gid, @PathVariable("aid") String aid)
            throws ResponseStatusException {

        try {
            Activity activity = activityServices.getGroupActivity(gid, aid);
            if(activity != null) {
                return activity;
            } else throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de la récupération de l'activité");
        } catch (ExecutionException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de la récupération de l'activité");
        }

    }

    @GetMapping("/{gid}")
    public Map<String, Activity> getGroupActivities(@PathVariable("gid") String gid)
            throws ResponseStatusException {

        try {
            return activityServices.getGroupActivities(gid);
        } catch (ExecutionException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de la récupération des activités");
        }

    }

    @DeleteMapping("/{gid}/{aid}")
    public String deleteActivity(@PathVariable("gid") String gid, @PathVariable("aid") String aid) {
        return activityServices.deleteActivity(gid, aid);
    }

    @GetMapping("/calendar/{gid}")
    public String exportCalendar(@PathVariable("gid") String gid)
            throws ResponseStatusException {

        try {
            return activityServices.exportCalendar(gid);
        } catch (ExecutionException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de l'exportation du calendrier");
        }

    }
}
