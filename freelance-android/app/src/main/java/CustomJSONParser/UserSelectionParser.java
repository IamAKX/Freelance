package CustomJSONParser;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import DataModel.UserSelectionModel;

/**
 * Created by akash on 15/4/17.
 */

public class UserSelectionParser {
    String id = null,name = null, category = null, image = null, rate = null;
    boolean selected = false;

    UserSelectionModel model = null;

    public UserSelectionModel getModel(JSONObject object)
    {

        try {
            if(object.has("_id"))
                id = object.getString("_id");
            if(object.has("name"))
                name = object.getString("name");

            if(object.has("category"))
            {
                category = object.getString("category").replace("[","").replace("]","").replace("\"","").trim();
                String[] categoryArray = category.split(",");
                Log.i("checking",categoryArray[0].toString());
                category = TextUtils.join("  \u2022  ",categoryArray);
                if(category.trim().length() == 0)
                    category = "No category is selected by "+name;
            }

            if(object.has("image"))
                image = object.getString("image");

            rate = "4.5";

            model = new UserSelectionModel(id,image,name,category,selected,Float.parseFloat(rate));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return model;
    }

}
