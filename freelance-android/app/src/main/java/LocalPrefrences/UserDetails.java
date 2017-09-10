package LocalPrefrences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by akash on 4/2/17.
 */

public class UserDetails {

    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public UserDetails(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PreferenceKey.USER_DETAILS, Activity.MODE_PRIVATE);
    }

    // User Name
    public void setUserName(String name)
    {
        editor = preferences.edit();
        editor.putString(PreferenceKey.USER_NAME,name);
        editor.commit();
    }

    public String getUserName()
    {
        return preferences.getString(PreferenceKey.USER_NAME,PreferenceKey.NO_NAME_FOUND);
    }

    // User Email
    public void setUserEmail(String email)
    {
        editor = preferences.edit();
        editor.putString(PreferenceKey.USER_EMAIL,email);
        editor.commit();
    }

    public String getUserEmail()
    {
        return preferences.getString(PreferenceKey.USER_EMAIL,PreferenceKey.NO_EMAIL_FOUND);
    }

    // User Image
    public void setUserImage(String image)
    {
        editor = preferences.edit();
        editor.putString(PreferenceKey.USER_IMAGE, image);
        editor.commit();
    }

    public String getUserImage()
    {
        return preferences.getString(PreferenceKey.USER_IMAGE,PreferenceKey.NO_PROFILE_IMAGE_FOUND);
    }

    // User category
    public void setUserCategory(String categoryArray)
    {
        editor = preferences.edit();
        editor.putString(PreferenceKey.USER_CATEGORY, categoryArray);
        editor.commit();
    }

    public String getUserCategory()
    {
        return preferences.getString(PreferenceKey.USER_CATEGORY,PreferenceKey.NO_KEY_FOUND);
    }

    //User mode

    public void setUserMode(String mode)
    {
        editor = preferences.edit();
        editor.putString(PreferenceKey.USER_MODE, mode);
        editor.commit();
    }

    public String getUserMode()
    {
        return preferences.getString(PreferenceKey.USER_MODE,PreferenceKey.NO_KEY_FOUND);
    }


    //profile Percent

    public void setProfilePercent(String percent)
    {
        editor = preferences.edit();
        editor.putString(PreferenceKey.USER_PROFILE_PERCENT, percent);
        editor.commit();
    }

    public int getProfilePercent()
    {
        int percent =  Integer.parseInt(preferences.getString(PreferenceKey.USER_PROFILE_PERCENT,PreferenceKey.ZERO_TIME));
        return percent;
    }

    //Remove all details
    public void removeUserDetails()
    {
        editor = preferences.edit();
        editor.remove(PreferenceKey.USER_NAME);
        editor.remove(PreferenceKey.USER_EMAIL);
        editor.remove(PreferenceKey.USER_IMAGE);
        editor.remove(PreferenceKey.USER_CATEGORY);
        editor.apply();
        editor.commit();
    }
}
