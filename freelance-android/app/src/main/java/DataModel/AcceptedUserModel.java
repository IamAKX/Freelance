package DataModel;

/**
 * Created by akash on 21/4/17.
 */

public class AcceptedUserModel {
    String id, name, image, chatroomId,acceptedState;

    public AcceptedUserModel(String id, String name, String image, String chatroomId, String acceptedState) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.chatroomId = chatroomId;
        this.acceptedState = acceptedState;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public String getAcceptedState() {
        return acceptedState;
    }
}
