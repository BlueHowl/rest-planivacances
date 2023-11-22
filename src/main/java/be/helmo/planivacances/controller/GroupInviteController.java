package be.helmo.planivacances.controller;

import be.helmo.planivacances.model.dto.GroupInviteDTO;
import be.helmo.planivacances.service.GroupInviteService;
import be.helmo.planivacances.service.GroupService;
import be.helmo.planivacances.service.UserService;
import com.google.firebase.auth.FirebaseAuthException;
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

    @Autowired
    private GroupService groupServices;

    @Autowired
    private UserService userServices;

    @GetMapping
    public List<GroupInviteDTO> getUserGroupInvites(HttpServletRequest request) {
        String uid = (String) request.getAttribute("uid");

        try {
            return groupInviteServices.listUserGroupInvites(uid);
        } catch (ExecutionException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de la récupération es invitations");
        }
    }

    @PostMapping("/{gid}/{mail}")
    public boolean inviteUser(@PathVariable("gid") String gid,
                                      @PathVariable("mail") String mail) throws ResponseStatusException {
        try {
            String uid = userServices.findUserUidByEmail(mail);

            if(groupServices.isInGroup(uid, gid)) {
                throw new ResponseStatusException(HttpStatus.ALREADY_REPORTED,
                        "L'utilisateur est déjà dans le groupe");
            }

            if (groupInviteServices.ChangeUserGroupLink(gid, uid, false)) {
                return true;
            } else throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de l'envoi de l'invitation");
        } catch (FirebaseAuthException | ExecutionException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT,
                    "Erreur lors de l'envoi de l'invitation");
        }
    }

    @PostMapping("/{gid}")
    public boolean acceptGroupInvite(@PathVariable("gid") String gid,
                                     HttpServletRequest request) throws ResponseStatusException {

        String uid = (String) request.getAttribute("uid");

        try {
            if(groupServices.updateGroupUserCount(gid, 1) &&
                    groupInviteServices.ChangeUserGroupLink(gid, uid, true)) {
                return true;
            } else throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED,
                    "Erreur lors de l'acceptation de l'invitation");
        } catch (ExecutionException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de l'acceptation de l'invitation");
        }
    }

    @DeleteMapping("/{gid}")
    public String declineGroupInvite(@PathVariable("gid") String gid,
                                     HttpServletRequest request) throws ResponseStatusException {

        String uid = (String) request.getAttribute("uid");

        return groupInviteServices.deleteGroupInvite(uid, gid);
    }

}
