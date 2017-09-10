package CustomJSONParser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bishal on 3/5/2017.
 */

public class ModeParser {
    String mode,reply;
    boolean status;

    public ModeParser(String reply) {
        this.reply = reply;
        parseMode();
    }

    public String getMode() {
        return mode;
    }

    public boolean getStatus() {
        return status;
    }

    private void parseMode() {
        try {
            JSONObject obj = new JSONObject(reply);
            if(obj.has("status"))
                status = obj.getBoolean("status");
            if(obj.has("mode"))
                mode = obj.getString("mode");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
