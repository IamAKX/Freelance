package DataModel;

/**
 * Created by akash on 9/5/17.
 */

public class ChatListModel {
    String name, image, chatroomId, userID, phone, email;

    public ChatListModel(String name, String image, String chatroomId, String userID, String phone, String email) {
        this.name = name;
        this.image = image;
        this.chatroomId = chatroomId;
        this.userID = userID;
        this.phone = phone;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
