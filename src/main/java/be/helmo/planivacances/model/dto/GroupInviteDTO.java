package be.helmo.planivacances.model.dto;

public class GroupInviteDTO {

    private String gid;
    private String groupName;

    public GroupInviteDTO(String gid, String groupName) {
        this.gid = gid;
        this.groupName = groupName;
    }

    //getters

    public String getGid() {
        return gid;
    }

    public String getGroupName() {
        return groupName;
    }

    //setters

    public void setGid(String gid) {
        this.gid = gid;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
