package LocalPrefrences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by akash on 25/5/17.
 */

public class EmailOTPHandler {

        Context context;
        SharedPreferences preferences;
        SharedPreferences.Editor editor;

        public EmailOTPHandler(Context context) {
            this.context = context;
            preferences = context.getSharedPreferences(PreferenceKey.OTP_FILE, Activity.MODE_PRIVATE);
        }

        public void setOtpNumber(String key)
        {
            editor = preferences.edit();
            editor.putString(PreferenceKey.EMAIL_OTP_NUMBER,key);
            editor.commit();
        }

        public String getOtpNumber()
        {
            return preferences.getString(PreferenceKey.EMAIL_OTP_NUMBER,PreferenceKey.NO_KEY_FOUND);
        }

        public void removeOtpNumber()
        {
            editor = preferences.edit();
            editor.remove(PreferenceKey.EMAIL_OTP_NUMBER);
            editor.apply();
            editor.commit();
        }

}
