package be.helmo.planivacances.controller;

import be.helmo.planivacances.model.Place;
import be.helmo.planivacances.service.AuthService;
import be.helmo.planivacances.service.PlaceService;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/place")
public class PlaceController {

    @Autowired
    private PlaceService placeServices;

    @Autowired
    private AuthService authServices;

    @PostMapping("/{gid}")
    public String createPlace(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody Place place,
            @PathVariable("gid") String gid)
            throws ExecutionException, InterruptedException, FirebaseAuthException {

        if (authServices.verifyToken(authorizationHeader) != null) {

            return placeServices.createPlace(gid, place);
        }

        return "\"Token invalide\"";
    }

    @GetMapping("/{gid}/{pid}")
    public Place getGroupPlace(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("gid") String gid,
            @PathVariable("pid") String pid)
            throws ExecutionException, InterruptedException, FirebaseAuthException {

        if (authServices.verifyToken(authorizationHeader) != null) {

            return placeServices.getPlace(gid, pid);
        }

        return null;
    }

    @GetMapping("/list/{gid}")
    public List<Place> getPlaces(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("gid") String gid)
            throws ExecutionException, InterruptedException, FirebaseAuthException {

        if (authServices.verifyToken(authorizationHeader) != null) {

            return placeServices.getGroupPlaces(gid);
        }

        return null;
    }

    @DeleteMapping("/{gid}/{pid}")
    public String deletePlace(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("gid") String gid,
            @PathVariable("pid") String pid) throws FirebaseAuthException {

        if (authServices.verifyToken(authorizationHeader) != null) {

            return placeServices.deletePlace(gid, pid);
        }

        return null;
    }
}
