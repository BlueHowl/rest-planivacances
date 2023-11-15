package be.helmo.planivacances.model.dto;

public class GroupMessageDTO {
    private String sender;
    private String displayName;
    private String groupId;
    private String content;
    private int time;

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

    public int getTime() {
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

    public void setTime(int time) {
        this.time = time;
    }
}