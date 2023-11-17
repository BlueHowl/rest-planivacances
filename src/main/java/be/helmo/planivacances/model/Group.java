package be.helmo.planivacances.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class Group {
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
    @NotBlank
    @NotNull
    private String placeId;
    private boolean isPublished;
    @NotBlank
    @NotNull
    private String owner;

    public Group() {}

    public Group(String groupName,
                 String description,
                 Date startDate,
                 Date endDate,
                 String placeId,
                 boolean isPublished,
                 String owner) {
        this.groupName = groupName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.placeId = placeId;
        this.isPublished = isPublished;
        this.owner = owner;
    }

    //getters
    /*public String getUid() {
        return uid;
    }*/

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
    /*public void setUid(String uid) {
        this.uid = uid;
    }*/

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
