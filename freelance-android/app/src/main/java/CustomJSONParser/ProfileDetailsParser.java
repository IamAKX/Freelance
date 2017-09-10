package CustomJSONParser;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by akash on 4/4/17.
 */

public class ProfileDetailsParser {
    String reply, name, email, userid, phone, image, address, category, experience, qualification, highestQualification;
    boolean fav;
    public ProfileDetailsParser(String reply) {
        this.reply = reply;
        setHighestQualification();
    }

    public boolean isFav() {
        try {
            JSONObject object = new JSONObject(reply);
            if(object.has("isFavourite"))
                fav = object.getBoolean("isFavourite");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return fav;
    }


    public void setHighestQualification() {
        JSONArray array = null;
        try {
            JSONObject object = new JSONObject(reply);
            if(object.has("qualification")) {
                array = object.getJSONArray("qualification");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (array.length()) {
            case 1:
                highestQualification = "Primary passed.";
                break;
            case 2:
                highestQualification = "Secondary passed.";
                break;
            case 3:
                highestQualification = "Higher Secondary passed.";
                break;
            case 4:
                highestQualification = "Graduation passed.";
                break;
            case 5:
                highestQualification = "Post Graduation passed.";
                break;
            default:
                highestQualification = "";
                break;
        }
    }

    public String getHighestQualification() {
        if(highestQualification.equalsIgnoreCase(""))
            highestQualification = "<b>--</b>";
        else
            highestQualification = "<i>I am <b><font color=\"teal\">"+highestQualification+"</font></b></i>";
        return highestQualification;
    }

    public String getName() {
        try {
            JSONObject object = new JSONObject(reply);
            if(object.has("name"))
                name = object.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return name;
    }

    public String getEmail() {
        try {
            JSONObject object = new JSONObject(reply);
            if(object.has("userName"))
                email = object.getString("userName");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return email;
    }

    public String getUserid() {
        try {
            JSONObject object = new JSONObject(reply);
            if(object.has("_id"))
                userid = object.getString("_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userid;
    }

    public String getPhone() {
        try {
            JSONObject object = new JSONObject(reply);
            if(object.has("phone"))
                phone = object.getString("phone");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return phone;
    }

    public String getImage() {
        try {
            JSONObject object = new JSONObject(reply);
            if(object.has("image"))
                image = object.getString("image");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return image;
    }

    public String getAddress() {
        try {
            address = "";
            JSONObject object = new JSONObject(reply);
            if(object.has("address"))
            {
                JSONObject a = object.getJSONObject("address");
                if(a.getString("type").equals("1"))
                    address += "<b>Residential</b> address is <br /><br />";
                else
                    address += "<b>Official</b> address is <br /><br />";

                address += a.getString("street")+",<br />";
                address += a.getString("city")+" ";
                address += a.getString("pincode")+",<br />";
                address += a.getString("state")+".<br />";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(address.trim().equalsIgnoreCase(""))
            address = "<i>Address not available</i>";
        return address;
    }

    public String getCategory() {
        JSONArray array = null;
        try {
            JSONObject object = new JSONObject(reply);
            if(object.has("category"))
            {
                category = object.getString("category").replace("[","").replace("]","").replace("\"","").trim();
                String[] categoryArray = category.split(",");
                Log.i("checking",categoryArray[0].toString());
                //category = TextUtils.join("\u25CB",categoryArray);
                category = "";
                for (String c : categoryArray)
                    category += "\u25CB "+c+"\n";
            }
            //userid = array.join("\n");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(category.trim().equalsIgnoreCase("\u25CB"))
            category = "No category is selected";
        return category;
    }

    public String getExperience() {
        JSONArray array = null;
        try {
            JSONObject object = new JSONObject(reply);
            if(object.has("experience"))
                array = object.getJSONArray("experience");
            experience = "";
            for(int i=0;i<array.length();i++)
            {
                String l ="";
                JSONObject e = array.getJSONObject(i);
                experience += "\u25CB Expertiese in <b><font color = \"teal\">"+e.getString("category")+"</font></b> <br />";
                experience += "&nbsp&nbsp&nbsp Details : <i>"+e.getString("details")+"</i> <br />";

                if(!e.getString("link").toString().equals(""))
                {
                    Log.i("checking",e.getString("link").toString());

                    String[] links = e.getString("link").toString().split(",");

                    for(int j = 0; j< links.length;j++)
                    {
                        if(links[j].indexOf("http")<0)
                            links[j] = "http://"+links[j];
                        l += "<a href="+links[j]+">CLICK"+(j+1)+"</a>";
                        if(j<links.length-1)
                            l += " , ";
                    }
                        //l+= links[j]+"  ";
                }
                experience += "&nbsp&nbsp&nbsp Project ref : <i>"+l+"</i> <br />";
                l = "";
                if(!e.getString("imageLink").toString().equals(""))
                {
                    String[] links = e.getString("imageLink").toString().split(",");

                    for(int j = 0; j< links.length;j++)
                    {
                        if(links[j].indexOf("http")<0)
                            links[j] = "http://"+links[j];
                        l += "<a href="+links[j]+">CLICK"+(j+1)+"</a>";
                        if(j<links.length-1)
                            l += " , ";
                    }

                }
                experience += "&nbsp&nbsp&nbsp Image ref : <i>"+l+"</i> <br />";
                if(i<array.length()-1)
                    experience+="<br />";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(experience.trim().equalsIgnoreCase(""))
            experience = "<i>No experience information is submitted.</i>";
        return experience;
    }

    public String getQualification() {
        JSONArray array = null;
        try {
            JSONObject object = new JSONObject(reply);
            if(object.has("qualification")) {
                array = object.getJSONArray("qualification");
            }

            qualification = "";
            for(int i = array.length()-1; i>=0; i--)
            {
                JSONObject q = array.getJSONObject(i);
                qualification += "<b>"+q.getString("stage")+"</b> from <i><b>"+q.getString("instName")+"</b></i> ("+q.getString("board")+" - "+q.getString("yop")+") with <b>"+q.getString("marks")+" " + q.getString("marksType")+"</b> <br/><br/>";
                //qualification += "\u25CB  "+q.getString("stage")+" from "+q.getString("instName")+" ("+q.getString("board")+" - "+q.getString("yop")+") with "+q.getString("marks")+" " + q.getString("marksType")+" \n";
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("checking",qualification);
        if(qualification.trim().equalsIgnoreCase(""))
            qualification = "No qualification information is available.";
        return qualification;
    }
}
