package be.helmo.planivacances.model.dto;

import be.helmo.planivacances.model.Group;
import be.helmo.planivacances.model.Place;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.cloud.firestore.annotation.Exclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class GroupDTO {

    @Exclude
    private String gid;
    @NotBlank
    @Size(min = 3, message = "Le nom du groupe de vacances doit faire minimum 3 caractères")
    private String groupName;
    private String description;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Date startDate;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Date endDate;
    @NotNull
    private Place place;
    private String owner;

    public GroupDTO() {}

    public GroupDTO(String gid,
                 String groupName,
                 String description,
                 Date startDate,
                 Date endDate,
                 Place place,
                 String owner) {
        this.gid = gid;
        this.groupName = groupName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.place = place;
        this.owner = owner;
    }

    public GroupDTO(Group g, String gid) {
        this.gid = gid;
        this.groupName = g.getGroupName();
        this.description = g.getDescription();
        this.startDate = g.getStartDate();
        this.endDate = g.getEndDate();
        this.place = null;
        this.owner = g.getOwner();
    }

    //getters
    public String getGid() {
        return gid;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getDescription() {
        return description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Place getPlace() {
        return place;
    }

    public String getOwner() {
        return owner;
    }


    //setters
    public void setGid(String gid) {
        this.gid = gid;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}

