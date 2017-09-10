package CustomJSONParser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by akash on 10/2/17.
 */

public class AddressDetailsParser {

    int type;
    String street,city,pincode,state,serverReply;

    public AddressDetailsParser(String serverReply) {
        this.serverReply = serverReply;
        parseServerReply(serverReply);
    }

    private void parseServerReply(String serverReply) {
        try {
            JSONObject object = new JSONObject(serverReply);
            String addressObj = object.getString("address");
            object = new JSONObject(addressObj);
            street = object.getString("street");
            city = object.getString("city");
            pincode = object.getString("pincode");
            state = object.getString("state");
            type = Integer.parseInt(object.getString("type"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getType() {
        return type;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getPincode() {
        return pincode;
    }

    public String getState() {
        return state;
    }
}
