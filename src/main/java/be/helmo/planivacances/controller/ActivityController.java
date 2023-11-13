package be.helmo.planivacances.controller;

import be.helmo.planivacances.service.ActivityService;
import be.helmo.planivacances.service.AuthService;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityServices;

    @Autowired
    private AuthService authServices;

    @PostMapping
    public String createActivity(
            @RequestHeader("Authorization") String authorizationHeader) throws ExecutionException, InterruptedException, FirebaseAuthException {

        return null;
    }

    @GetMapping("calendar/{gid}")
    public String exportCalendar(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("gid") String gid) throws FirebaseAuthException, ExecutionException, InterruptedException {

        if(authServices.verifyToken(authorizationHeader) != null) {
            return activityServices.exportCalendar(gid);
        }

        return null;
    }
}
