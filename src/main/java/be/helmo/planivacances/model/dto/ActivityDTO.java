package be.helmo.planivacances.model.dto;

import be.helmo.planivacances.model.Place;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

public class ActivityDTO {
    @NotBlank
    @Size(min = 3, message = "Le nom de l'activité doit faire minimum 3 caractères")
    private String title;
    private String description;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Date startDate;
    @NotNull
    private int duration;
    @NotBlank
    private String placeId;


    //getters

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public int getDuration() {
        return duration;
    }

    public String getPlaceId() {
        return placeId;
    }


    //setters

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

}
