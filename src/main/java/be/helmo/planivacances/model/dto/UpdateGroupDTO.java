package be.helmo.planivacances.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class UpdateGroupDTO {

    @NotNull
    private String gid;
    @NotNull
    @Size(min = 4, message = "Le nom du groupe de vacances doit faire minimum 3 caractères")
    private String groupName;
    private String description;
    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date startDate;
    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date endDate;
    @NotNull
    private String placeId;
    private boolean isPublished;
    @NotNull
    private String owner;


    //getters
    public String gGid() {
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

    public String getPlaceId() {
        return placeId;
    }

    public boolean isPublished() {
        return isPublished;
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

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
