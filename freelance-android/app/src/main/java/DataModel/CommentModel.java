package DataModel;

/**
 * Created by akash on 22/5/17.
 */

public class CommentModel {
    String uid, name, image, rating, comment;

    public CommentModel(String uid, String name, String image, String rating, String comment) {
        this.uid = uid;
        this.name = name;
        this.image = image;
        this.rating = rating;
        this.comment = comment;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }
}
