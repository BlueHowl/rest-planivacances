package be.helmo.restplanivacances.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Group {

    private String uid;
    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    private boolean isPublished;


    public String getUid() {
        return uid;
    }

    public String getTitle() {
        return title;
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

    public void setPublished(boolean published) {
        isPublished = published;
    }


    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public boolean isPublished() {
        return isPublished;
    }
}
