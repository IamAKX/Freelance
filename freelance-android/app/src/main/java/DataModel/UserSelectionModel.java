package DataModel;

/**
 * Created by akash on 15/4/17.
 */

public class UserSelectionModel {
    String id, image, name, category;
    boolean selectedState;
    float rating;

    public UserSelectionModel(String id, String image, String name, String category, boolean selectedState, float rating) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.category = category;
        this.selectedState = selectedState;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isSelectedState() {
        return selectedState;
    }

    public void setSelectedState(boolean selectedState) {
        this.selectedState = selectedState;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}