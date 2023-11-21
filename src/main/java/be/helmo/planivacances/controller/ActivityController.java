package be.helmo.planivacances.controller;

import be.helmo.planivacances.service.ActivityService;
import be.helmo.planivacances.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityServices;

    @PostMapping
    public String createActivity() {

        return null;
    }

    @GetMapping("calendar/{gid}")
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
