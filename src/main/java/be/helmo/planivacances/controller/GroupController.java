package be.helmo.planivacances.controller;

import be.helmo.planivacances.model.Group;
import be.helmo.planivacances.model.Place;
import be.helmo.planivacances.model.dto.GroupAndPlaceDTO;
import be.helmo.planivacances.service.AuthService;
import be.helmo.planivacances.service.GroupService;
import be.helmo.planivacances.service.PlaceService;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/group")
public class GroupController {

    @Autowired
    private GroupService groupServices;

    @Autowired
    private PlaceService placeServices;

    @Autowired
    private AuthService authServices;

    @PostMapping
    public String createGroup(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody GroupAndPlaceDTO gp) throws ExecutionException, InterruptedException, FirebaseAuthException {

        String uid = authServices.verifyToken(authorizationHeader);

        if(uid != null) {
            Group group = gp.getGroup();
            Place place = gp.getPlace();

            group.setOwner(uid);
            String gid = groupServices.createGroup(group);

            placeServices.createPlace(gid, place);

            return gid;
        }

        return null;
    }

    @GetMapping("/{gid}")
    public Group getGroup(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("gid") String gid) throws ExecutionException, InterruptedException, FirebaseAuthException {
        if (authServices.verifyToken(authorizationHeader) != null) {

            return groupServices.getGroup(gid);
        }

        return null;
    }

    @GetMapping("/list")
    public List<Group> getGroups(@RequestHeader("Authorization") String authorizationHeader) throws ExecutionException, InterruptedException, FirebaseAuthException {
        if (authServices.verifyToken(authorizationHeader) != null) {
            return groupServices.getGroups();

        }

        return null;
    }

    //!! Avec firebase l'update se fait de la même maniére que le create, il se base sur l'id du document donc si il
    //existe il est update snn il est créé
    @PutMapping("/{gid}")
    public String updateGroup(@RequestHeader("Authorization") String authorizationHeader,
                              @RequestBody Group group,
                              @PathVariable("gid") String gid) throws ExecutionException, InterruptedException, FirebaseAuthException {
        if (authServices.verifyToken(authorizationHeader) != null) {

            /*Group group = new Group(groupDTO.getGroupName(),
                    groupDTO.getDescription(),
                    groupDTO.getStartDate(),
                    groupDTO.getEndDate(),
                    groupDTO.getPlaceId(),
                    groupDTO.isPublished(),
                    groupDTO.getOwner());*/

            return groupServices.updateGroup(gid, group);
        }

        return null;
    }

    @DeleteMapping("/{gid}")
    public String deleteGroup(@RequestHeader("Authorization") String authorizationHeader,
                              @PathVariable("gid") String gid) throws FirebaseAuthException {
        if (authServices.verifyToken(authorizationHeader) != null) {

            return groupServices.deleteGroup(gid);
        }

        return null;
    }
}
