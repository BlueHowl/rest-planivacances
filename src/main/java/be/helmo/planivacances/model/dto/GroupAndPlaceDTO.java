package be.helmo.planivacances.model.dto;

import be.helmo.planivacances.model.Group;
import be.helmo.planivacances.model.Place;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GroupAndPlaceDTO {
    @NotNull
    private Group group;
    @NotNull
    private Place place;

    public Group getGroup() {
        return group;
    }

    public Place getPlace() {
        return place;
    }

    public void setGroup(Group group, Place place) {
        this.group = group;
        this.place = place;
    }
}
