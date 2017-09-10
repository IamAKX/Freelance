package CustomJSONParser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by akash on 20/4/17.
 */

public class PostedProjectDetailsParser {

    String reply;
    String deadLine = "deadline";
    public PostedProjectDetailsParser(String reply) {
        this.reply = reply;
    }

    public String getName() {
        try {
            JSONObject object = new JSONObject(reply);
            return object.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "name";
    }

    public String getDesc() {
        try {
            JSONObject object = new JSONObject(reply);
            return object.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "description";
    }

    public String getDuration() {
        try {
            JSONObject object = new JSONObject(reply);
            object = object.getJSONObject("duration");
            deadLine = object.getString("deadline");
            return object.getString("start")+" - "+object.getString("end")+"  "+object.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "duration";
    }

    public String getDeadline() {
        return deadLine;
    }

    public String getCreatedOn() {
        try {
            JSONObject object = new JSONObject(reply);
            return object.getString("createdAt");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "date";
    }

    public String getStatus() {
        try {
            JSONObject object = new JSONObject(reply);
            return object.getString("projectStatus");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "In Progess..";
    }
}
