package CustomJSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akash on 19/2/17.
 */

public class ExperienceDetailsParser {
    String id,category,details,link,image[], reply;

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getDetails() {
        return details;
    }

    public String getLink() {
        return link;
    }

    public String[] getImage() {
        return image;
    }


    public static JSONArray getExperienceArray(String response) {
        JSONObject object = null;
        JSONArray array = new JSONArray();
        try {
            object = new JSONObject(response);
            array = object.getJSONArray("experience");
        } catch (JSONException e) {
        }
        return array;
    }

    public ExperienceDetailsParser(String reply) {
        this.reply = reply;
        parseDetail(reply);

    }

    private void parseDetail(String reply) {
        try {
            JSONObject object = new JSONObject(reply);
            id = object.getString("_id");
            category = object.getString("category");
            details = object.getString("details");
            link = object.getString("link");
            image = object.getString("imageLink").replace("\\","").split(",");
            //convertJsonArrayToArray(object.getJSONArray("imageLink"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void convertJsonArrayToArray(JSONArray imageLink) {

        List<String> stringList = new ArrayList<>();
        try {
            for(int i=0;i<imageLink.length();i++) {
                stringList.add(imageLink.getString(i).replace("\\", ""));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        image = new String[stringList.size()];
        image =  stringList.toArray(new String[stringList.size()]);

    }
}
