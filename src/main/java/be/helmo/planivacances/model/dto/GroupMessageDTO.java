package be.helmo.planivacances.model.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class GroupMessageDTO {
    @NotBlank
    private String sender;
    @NotBlank
    private String displayName;
    @NotBlank
    private String groupId;
    @NotBlank
    private String content;
    @Min(0)
    private long time;

    public String getSender() {
        return sender;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getContent() {
        return content;
    }

    public long getTime() {
        return time;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(long time) {
        this.time = time;
    }
}