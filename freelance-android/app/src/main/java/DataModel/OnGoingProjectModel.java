package DataModel;

/**
 * Created by akash on 17/5/17.
 */

public class OnGoingProjectModel {
    String uid, pid, uname, uimage, uphone, deadline, postedDate, chatroomId;

    public OnGoingProjectModel(String uid, String pid, String uname, String uimage, String uphone, String deadline, String postedDate, String chatroomId) {
        this.uid = uid;
        this.pid = pid;
        this.uname = uname;
        this.uimage = uimage;
        this.uphone = uphone;
        this.deadline = deadline;
        this.postedDate = postedDate;
        this.chatroomId = chatroomId;
    }

    public String getUid() {
        return uid;
    }

    public String getPid() {
        return pid;
    }

    public String getUname() {
        return uname;
    }

    public String getUimage() {
        return uimage;
    }

    public String getUphone() {
        return uphone;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public String getChatroomId() {
        return chatroomId;
    }
}
