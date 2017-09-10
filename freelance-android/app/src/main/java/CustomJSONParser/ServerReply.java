package CustomJSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by akash on 1/2/17.
 */

public class ServerReply {
    boolean status;
    String reason;
    String token;

    public boolean getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public String getToken() {
        return token;
    }

    String reply;

    public ServerReply(String reply) {
        this.reply = reply;
        status = false;
        reason = null;
        token = null;
        replyParser();
    }

    private void replyParser() {
        try {
            JSONObject object = new JSONObject(reply);
            status = object.getBoolean("status");
            if(object.has("reason"))
                reason = object.getString("reason");
            if(object.has("token"))
                token = object.getString("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
