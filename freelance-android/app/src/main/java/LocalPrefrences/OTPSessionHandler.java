package LocalPrefrences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by akash on 3/2/17.
 */

public class OTPSessionHandler {
    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public OTPSessionHandler(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PreferenceKey.OTP_FILE, Activity.MODE_PRIVATE);
    }

    public void setOtpNumber(String key)
    {
        editor = preferences.edit();
        editor.putString(PreferenceKey.OTP_NUMBER,key);
        editor.commit();
    }

    public String getOtpNumber()
    {
        return preferences.getString(PreferenceKey.OTP_NUMBER,PreferenceKey.NO_KEY_FOUND);
    }

    public void removeOtpNumber()
    {
        editor = preferences.edit();
        editor.remove(PreferenceKey.OTP_NUMBER);
        editor.apply();
        editor.commit();
    }
}
