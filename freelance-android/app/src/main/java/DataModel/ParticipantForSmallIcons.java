package DataModel;

/**
 * Created by akash on 16/5/17.
 */

public class ParticipantForSmallIcons {
    String name, image, id;

    public ParticipantForSmallIcons(String name, String image, String id) {
        this.name = name;
        this.image = image;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getId() {
        return id;
    }
}
