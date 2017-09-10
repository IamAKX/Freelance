package DataModel;

/**
 * Created by akash on 29/3/17.
 */

public class EmployeeModel {
    String id, name, image, categories, ratings, phone;
    boolean fav,certified;

    public EmployeeModel(String id, String name, String image, String categories, String ratings, String phone, boolean fav, boolean certified) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.categories = categories;
        this.ratings = ratings;
        this.phone = phone;
        this.fav = fav;
        this.certified = certified;
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

    public String getCategories() {
        return categories;
    }

    public String getRatings() {
        return ratings;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isFav() {
        return fav;
    }

    public boolean isCertified() {
        return certified;
    }
}
