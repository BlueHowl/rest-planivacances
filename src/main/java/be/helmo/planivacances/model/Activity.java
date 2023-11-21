package be.helmo.planivacances.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

public class Activity {

    @NotNull
    @Size(min = 3, message = "Le nom de l'activité doit faire minimum 3 caractères")
    private String title;
    private String description;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Date startDate;
    @NotNull
    private int duration;
    @NotNull
    private Place place;

    public Activity(String title, String description, Date startDate, int duration, Place place) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.duration = duration;
        this.place = place;
    }


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

    public Place getPlace() {
        return place;
    }

    public String getPlaceAddress() {
        return place.getAddress();
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

    public void setPlace(Place place) {
        this.place = place;
    }
}
