package be.helmo.planivacances.model.firebase.dto;

import be.helmo.planivacances.model.dto.PlaceDTO;
import be.helmo.planivacances.model.dto.GroupDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class DBGroupDTO {

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
    private DBPlaceDTO place;
    private String owner;

    private int userCount = 1;

    public DBGroupDTO() {}

    public DBGroupDTO(GroupDTO g) {
        this.groupName = g.getGroupName();
        this.description = g.getDescription();
        this.startDate = g.getStartDate();
        this.endDate = g.getEndDate();
        this.place = new DBPlaceDTO(g.getPlace());
        this.owner = g.getOwner();
    }

    //getters
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

    public DBPlaceDTO getPlace() {
        return place;
    }

    public String getOwner() {
        return owner;
    }

    public int getUserCount() {
        return userCount;
    }

    //setters
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

    public void setPlace(DBPlaceDTO place) {
        this.place = place;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }
}

