package LocalPrefrences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

/**
 * Created by akash on 2/2/17.
 */

public class SessionData {
    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public SessionData(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PreferenceKey.SESSION_FILE, Activity.MODE_PRIVATE);
    }

    public void setSessionKey(String key)
    {
        editor = preferences.edit();
        editor.putString(PreferenceKey.SESSION_KEY,key);
        editor.commit();
    }

    public String getSessionKey()
    {
        return preferences.getString(PreferenceKey.SESSION_KEY,PreferenceKey.NO_KEY_FOUND);
    }

    public void removeSessionKey()
    {
        editor = preferences.edit();
        editor.remove(PreferenceKey.SESSION_KEY);
        editor.apply();
        editor.commit();
    }


}
