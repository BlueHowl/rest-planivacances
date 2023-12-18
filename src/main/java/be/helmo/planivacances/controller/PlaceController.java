package be.helmo.planivacances.controller;

import be.helmo.planivacances.model.dto.PlaceDTO;
import be.helmo.planivacances.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/place")
public class PlaceController {

    @Autowired
    private PlaceService placeServices;

    @PostMapping("/{gid}")
    public String createPlace(
            @RequestBody PlaceDTO place,
            @PathVariable("gid") String gid)
            throws ResponseStatusException {

        try {
            return placeServices.createOrGetPlace(gid, place);
        } catch (ExecutionException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la creation du lieu");
        }

    }

    @GetMapping("/{gid}/{pid}")
    public PlaceDTO getGroupPlace(
            @PathVariable("gid") String gid,
            @PathVariable("pid") String pid)
            throws ResponseStatusException {

        try {
            return placeServices.getPlace(gid, pid);
        } catch (ExecutionException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la recuperation du lieu de vacances");
        }

    }

    @GetMapping("/list/{gid}")
    public List<PlaceDTO> getPlaces(
            @PathVariable("gid") String gid)
            throws ResponseStatusException {

        try {
            return placeServices.getGroupPlaces(gid);
        } catch (ExecutionException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la recuperation des lieu");
        }

    }

    @DeleteMapping("/{gid}/{pid}")
    public String deletePlace(
            @PathVariable("gid") String gid,
            @PathVariable("pid") String pid) throws ResponseStatusException {

            return placeServices.deletePlace(gid, pid);

    }
}
