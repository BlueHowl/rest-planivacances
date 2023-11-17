package be.helmo.planivacances.controller;

import be.helmo.planivacances.model.dto.GroupDTO;
import be.helmo.planivacances.service.GroupInviteService;
import be.helmo.planivacances.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/group/invitation")
public class GroupInviteController {

    @Autowired
    private GroupInviteService groupInviteServices;

    @PostMapping("/invitation/{gid}/{uid}")
    public boolean inviteUser(@PathVariable("gid") String gid,
                                      @PathVariable("uid") String uid) throws ResponseStatusException {
        //TODO find user id by mail else error not found
        if(groupInviteServices.ChangeUserGroupLink(gid, uid, false)) {
            return true;
        } else throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de l'envoi de l'invitation");

    }

    @PostMapping("/invitation/{gid}")
    public boolean acceptGroupInvite(@PathVariable("gid") String gid,
                                     HttpServletRequest request) throws ResponseStatusException {

        String uid = (String) request.getAttribute("uid");

        if(groupInviteServices.ChangeUserGroupLink(gid, uid, true)) {
            return true;
        } else throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erreur lors de l'acceptation de la requête");

    }

    @DeleteMapping("/invitation/{gid}")
    public String declineGroupInvite(@PathVariable("gid") String gid,
                                     HttpServletRequest request) throws ResponseStatusException {

        String uid = (String) request.getAttribute("uid");

        return groupInviteServices.deleteGroupInvite(uid, gid);
    }

}
