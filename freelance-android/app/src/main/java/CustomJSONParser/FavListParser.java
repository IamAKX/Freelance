package CustomJSONParser;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import DataModel.EmployeeModel;

/**
 * Created by akash on 20/5/17.
 */

public class FavListParser
{

    public EmployeeModel getModel(JSONObject object) {
        String id = null,name = null,phone = null, category = null, image = null, rate = "4.5";
        boolean fav = false, certified = false;

        EmployeeModel model = null;
        try {
            if(object.has("_id"))
                id = object.getString("_id");
            if(object.has("name"))
                name = object.getString("name");

            if(object.has("phone"))
                phone = object.getString("phone");
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

            if(object.has("certified"))
                certified = object.getBoolean("certified");

            if(object.has("rate"))
                rate = object.getString("rate");

            fav = true;

            model = new EmployeeModel(id,name,image,category,rate,phone,fav,certified);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return model;
    }

}
