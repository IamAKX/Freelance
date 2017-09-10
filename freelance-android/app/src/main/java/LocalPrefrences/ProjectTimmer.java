package LocalPrefrences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by akash on 19/4/17.
 */

public class ProjectTimmer {
    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public ProjectTimmer(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PreferenceKey.PROJECT_TIMER, Activity.MODE_PRIVATE);
    }

    public void setTimmer(String key, String value)
    {
        editor = preferences.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public String getTimmer(String key)
    {
        return preferences.getString(key,PreferenceKey.ZERO_TIME);
    }
}
