package CustomJSONParser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by akash on 22/4/17.
 */

public class JobListParser {
    String id, name, desc, category, participant, creatAt, projectStatus;
    JSONObject object;

    public JobListParser(JSONObject object) throws JSONException {
        this.object = object;
        id = object.getString("_id");
        name = object.getString("name");
        projectStatus = object.getString("projectStatus");
        desc = object.getString("description");
        category = object.getString("category");
        participant = String.valueOf(object.getJSONArray("participant").length());
        creatAt = object.getString("createdAt");

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getCategory() {
        return category;
    }

    public String getParticipant() {
        return participant;
    }

    public String getCreatAt() {
        return creatAt;
    }

    public String getProjectStatus() {
        return projectStatus;
    }
}
