package LocalPrefrences;

/**
 * Created by akash on 2/2/17.
 */

public class PreferenceKey {

    //If data does not exist
    public static final int NO_INDEX_FOUND = 0;
    public static final String NO_KEY_FOUND = "keyisabsent";
    public static final String NO_PROFILE_IMAGE_FOUND = "https://cdn3.iconfinder.com/data/icons/user-avatars-1/512/users-10-3-256.png";
    public static final String NO_NAME_FOUND = "User";
    public static final String NO_EMAIL_FOUND = "user@email.domain";

    // File name and key for session
    public static final String SESSION_FILE = "usersession";
    public static final String SESSION_KEY = "sessionkey";
    public static final String APP_MODE = "appmode";

    //OTP Managing
    public static final String OTP_FILE = "currentotpfile";
    public static final String OTP_NUMBER = "currentotp";

    //Storing User details
    public static final String USER_DETAILS = "userdetailsfile";
    public static final String USER_NAME = "myname";
    public static final String USER_EMAIL = "myemail";
    public static final String USER_IMAGE = "myimage";
    public static final String USER_CATEGORY = "selectedcategory";
    public static final String USER_MODE = "mymode";

    public static final String PROJECT_TIMER = "projectTimmer";
    public static final String TIMMER_KEY = "timertimeleft";
    public static final String ZERO_TIME = "0";
    public static final String USER_PROFILE_PERCENT = "profPercent";
    public static final String EMAIL_OTP_NUMBER = "emailOTP";
}
