package be.helmo.planivacances.controller;

import be.helmo.planivacances.model.Group;
import be.helmo.planivacances.model.Place;
import be.helmo.planivacances.model.dto.GroupAndPlaceDTO;
import be.helmo.planivacances.service.AuthService;
import be.helmo.planivacances.service.GroupService;
import be.helmo.planivacances.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/group")
public class GroupController {

    @Autowired
    private GroupService groupServices;

    @Autowired
    private PlaceService placeServices;

    @PostMapping
    public String createGroup(
            HttpServletRequest request,
            @RequestBody GroupAndPlaceDTO gp) throws ResponseStatusException {

        String uid = (String) request.getAttribute("uid");

        try {
            if (uid != null) {
                Group group = gp.getGroup();
                Place place = gp.getPlace();

                group.setOwner(uid);
                String gid = groupServices.createGroup(group);
                String pid = placeServices.createPlace(gid, place);

                group.setPlaceId(pid);
                groupServices.updateGroup(gid, group);

                return gid;
            } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalide");
        } catch (ExecutionException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la creation du groupe");
        }

    }

    @GetMapping("/{gid}")
    public Group getGroup(@PathVariable("gid") String gid) throws ResponseStatusException {
            try {
                return groupServices.getGroup(gid);
            } catch (ExecutionException | InterruptedException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Erreur lors de la récupération du groupe");
            }
    }

    @GetMapping("/list")
    public List<Group> getGroups() throws ResponseStatusException {
        try {
            return groupServices.getGroups();
        } catch (ExecutionException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la recuperation des groupes");
        }

    }

    //!! Avec firebase l'update se fait de la même maniére que le create, il se base sur l'id du document donc si il
    //existe il est update snn il est créé
    @PutMapping("/{gid}")
    public String updateGroup(@RequestBody Group group,
                              @PathVariable("gid") String gid) throws ResponseStatusException {

        try {
            /*Group group = new Group(groupDTO.getGroupName(),
                    groupDTO.getDescription(),
                    groupDTO.getStartDate(),
                    groupDTO.getEndDate(),
                    groupDTO.getPlaceId(),
                    groupDTO.isPublished(),
                    groupDTO.getOwner());*/

            return groupServices.updateGroup(gid, group);
        } catch (ExecutionException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour du groupe");
        }

    }

    @DeleteMapping("/{gid}")
    public String deleteGroup(@PathVariable("gid") String gid) throws ResponseStatusException {
        return groupServices.deleteGroup(gid);
    }
}
