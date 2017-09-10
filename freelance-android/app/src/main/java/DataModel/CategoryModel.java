package DataModel;

/**
 * Created by akash on 16/5/17.
 */

public class CategoryModel
{
    String name, image;

    public CategoryModel(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
