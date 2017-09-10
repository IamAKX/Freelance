package CustomJSONParser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by akash on 9/5/17.
 */

public class ChatRoomListParser {
    String name, image, userID, chatroomId, email, phone, reply;

    public ChatRoomListParser(String reply) {
        this.reply = reply;
        try {
            JSONObject object = new JSONObject(reply);
            name = object.getString("name");
            image = object.getString("image");
            userID = object.getString("userId");
            chatroomId = object.getString("chatroomId");
            email = object.getString("username");
            phone = object.getString("phone");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getUserID() {
        return userID;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
