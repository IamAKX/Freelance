package CustomJSONParser;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by akash on 8/2/17.
 */

public class UpdateProfileImageParser {
    String imageName, serverReply;
    boolean status;

    public UpdateProfileImageParser(String serverReply) {
        this.serverReply = serverReply;
        parseReply();
    }

    public boolean getStatus() {
        return status;
    }

    public String getImageName() {
        return imageName;
    }

    private void parseReply() {
        JSONObject object = null;
        try {
            object = new JSONObject(serverReply);
            status = object.getBoolean("status");
            imageName = object.getString("userimage");
            Log.i("Checking","image "+imageName+"    "+status);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
