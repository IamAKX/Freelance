package CustomJSONParser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by akash on 11/2/17.
 */

public class OccupationDetailParser {
    boolean presentlyWorking;
    String position, organization, startDate, endDate, serverReply;

    public OccupationDetailParser(String serverReply) {
        this.serverReply = serverReply;
        presentlyWorking = false;
        parseServerReply();
    }

    private void parseServerReply() {
        try {
            JSONObject object = new JSONObject(serverReply);
            String details = object.getString("presentJob");
            object = new JSONObject(details);
            position = object.getString("position");
            organization = object.getString("organzation");
            startDate = object.getString("startDate");
            endDate = object.getString("endDate");
            presentlyWorking = new Boolean(object.getString("presentlyWorking"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isPresentlyWorking() {
        return presentlyWorking;
    }

    public String getPosition() {
        return position;
    }

    public String getOrganization() {
        return organization;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }
}
