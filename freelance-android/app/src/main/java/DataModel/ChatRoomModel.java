package DataModel;

/**
 * Created by akash on 9/5/17.
 */

public class ChatRoomModel {

    String msg, timeStamp;
    boolean me;

    public ChatRoomModel(String msg, String timeStamp, boolean me) {
        this.msg = msg;
        this.timeStamp = timeStamp;
        this.me = me;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isMe() {
        return me;
    }

    public void setMe(boolean me) {
        this.me = me;
    }
}
