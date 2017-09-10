package CustomJSONParser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by akash on 8/2/17.
 */

public class SignInParser {

    String image, userEmail, name, serverReply, category;

    public SignInParser(String serverReply) {

        this.serverReply = serverReply;
        image = null;
        userEmail = null;
        name = null;
        category = null;
        parseReply();

    }

    public String getImage() {
        return image;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getName() {
        return name;
    }

    private void parseReply() {
        try {
            JSONObject object = new JSONObject(serverReply);
            image = object.getString("userimage");
            userEmail = object.getString("username");
            name = object.getString("name");
            JSONArray array = new JSONArray(object.getString("catergory"));
            category = array.getString(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getCategory() {
        return category;
    }
}
