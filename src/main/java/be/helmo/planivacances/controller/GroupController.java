package be.helmo.planivacances.controller;

import be.helmo.planivacances.entity.Group;
import be.helmo.planivacances.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class GroupController {

    @Autowired
    private GroupService groupServices;

    @PostMapping("/groups")
    public String saveGroup(@RequestBody Group group) throws ExecutionException, InterruptedException {

        return groupServices.createOrUpdateGroup(group);
    }

    @GetMapping("/groups/{uid}")
    public Group getGroup(@PathVariable String uid) throws ExecutionException, InterruptedException {

        return groupServices.getGroup(uid);
    }

    @GetMapping("/groups")
    public List<Group> getGroups() throws ExecutionException, InterruptedException {

        return groupServices.getGroups();
    }

    //!! Avec firebase l'update se fait de la même maniére que le create, il se base sur l'id du document donc si il
    //existe il est update snn il est créé
    @PutMapping("/groups")
    public String updateGroup(@RequestBody Group group) throws ExecutionException, InterruptedException {

        return groupServices.createOrUpdateGroup(group);
    }

    @DeleteMapping("/groups/{uid}")
    public String deleteGroup(@PathVariable String uid) throws ExecutionException, InterruptedException {

        return groupServices.deleteGroup(uid);
    }
}
