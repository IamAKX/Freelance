package Utils;

import DataModel.CategoryModel;

/**
 * Created by akash on 16/5/17.
 */

public class CategoryIcon {
    public static String getIcon(String category)
    {
        for(CategoryModel model: Constants.data)
        {
            if(model.getName().equalsIgnoreCase(category))
                return model.getImage();
        }

        return null;
    }
}
