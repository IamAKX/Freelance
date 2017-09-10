package Utils;

import java.util.ArrayList;
import java.util.List;

import DataModel.CategoryModel;

/**
 * Created by akash on 29/1/17.
 */

public class Constants {
    //Permission Codes
    public static final int CALENDER_GROUP_CODE = 100;
    public static final int CAMERA_GROUP_CODE = 200;
    public static final int CONTACTS_GROUP_CODE = 300;
    public static final int LOCATION_GROUP_CODE = 400;
    public static final int MICROPHONE_GROUP_CODE = 500;
    public static final int PHONE_GROUP_CODE = 600;
    public static final int SENSORS_GROUP_CODE = 700;
    public static final int SMS_GROUP_CODE = 800;
    public static final int STORAGE_GROUP_CODE = 900;

//    public static final String IP = "http://139.59.30.223";
    public static final String IP = "http://freelance.greyphase.xyz";

    public static final String BASE_URL = IP + "/api";

    public static final String VERIFY_EMAIL = BASE_URL+"/user/checkUserValidity";
    public static final String VERIFY_PHONE = BASE_URL+"/user/checkPhoneValidity";

    public static final String REGISTER = BASE_URL+"/user/create";

    public static final String SEND_OTP = BASE_URL + "/otp/genOtp";
    public static final String VERIFY_OTP = BASE_URL + "/otp/verifyOtp";

    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    //public static final String Email_Verification_URL = "http://akashapplications.hol.es/freelance/emailrecovery.php";

    public static final String LOGIN = BASE_URL + "/user/login";
    public static final String LOGIN_SESSION = BASE_URL + "/user/test";

    public static final String SMS_ANY_API = "5e04299d9696d6e3e0e8e7ae8c99cbf8369a6e07";
    public static final String SMS_ANY_URL = "https://smsany.herokuapp.com/api/sms/sendsms";

    public static final String[] ACCOUNT_MENU_LIST = {"Change Phone Number","Change Email","Change Password","Delete Account"};

    public static final String[] PROFILE_MENU_LIST = {"Application Mode","Category","Address","Education Details","Occupation","Experience"};

    public static final int PICK_FROM_GALLERY = 999;
    public static final int PICK_FROM_CAMERA = 888;

    //Update Profile Images
    public static final String UPLOAD_PROFILE_IMAGE = BASE_URL+"/userData/uploadImage";
    public static final String USER_CURRENT_PROFILE_IMAGE = IP + "/images/";

    //Address Inputs
    public static final String[] ADDRESS_CARD_LOCATION_TYPE = {"Select Type","Residential","Official"};
    public static final String SAVE_ADDRESS = BASE_URL + "/userData/setAddress";
    public static final String FETCH_ADDRESS = BASE_URL + "/userData/getAddress";

    //Education Details
    public static final String[] HIGHEST_QUALIFICATION_CATEGORY = {"-N/A-","Standard V Passed","Standard X Passed","Standard XII Passed","Graduate","Post Graduate"};
    public static final String[] MARKS_UNIT = {"--Unit--","%","CGPA"};

    //Occupation
    public static final String SAVE_OCCUPATION = BASE_URL + "/userData/setPresentJob";
    public static final String FETCH_OCCUPATION = BASE_URL + "/userData/getPresentJob";

    public static final String SAVE_QUALIFICATION = BASE_URL + "/userData/setQualification";
    public static final String FETCH_QUALIFICATION = BASE_URL + "/userData/getQualification";

    public static final String INSERT_EXPERIENCE = BASE_URL + "/userData/insertExperience";
    public static final String UPDATE_EXPERIENCE = BASE_URL + "/userData/updateExperience";
    public static final String FETCH_EXPERIENCE = BASE_URL + "/userData/getExperience";
    public static final String SAVE_MODE = BASE_URL + "/userData/setMode";
    public static final String GET_MODE = BASE_URL + "/userData/getMode";
    public static final String SAVE_CATEGORY = BASE_URL + "/userData/setCategory";

    public static final String FETCH_WORKER = BASE_URL + "/public/users/all";
    public static final String VERIFY_EMAIL_TOKEN = BASE_URL + "/auth/verifyEmailAccountFromApp" ;
    public static final String PROFILE_DETAILS = BASE_URL + "/public/users/detail";
    public static final String RESEND_EMAIL_VERIFICATIONLINK = BASE_URL + "/auth/resendEmailVerify";
    public static final String BROADCAST_PROJECT = BASE_URL + "/project/postProject";
    public static final String FCM_TOKEN_UPDATE = BASE_URL + "/token/setToken";
    public static final String FETCH_ALL_PROJECT_LIST = BASE_URL +"/project/getAllProjects";

    public static final String VIEW_POSTED_PROJECT_DETAILS = BASE_URL + "/project/getProjectDetails/employer";
    public static final String VIEW_JOB_DETAILS = BASE_URL + "/project/getProjectDetails/worker";
    public static final String FETCH_ALL_JOB_LIST = BASE_URL + "/project/allProjects";
    public static final String ACCEPT_PROJECT = BASE_URL + "/project/acceptProject";
    public static final String REJECT_PROJECT = BASE_URL + "/project/rejectProject";
    public static final String CHANGE_PROJECT_STATUS_AFTER_WAITING_TIME = BASE_URL + "/project/timeout";
    public static final String FETCH_CATEGORY = BASE_URL + "/constant/getallCategory";
    public static final String GET_UPLOADED_APP = BASE_URL + "/app/getUploadedApp";
    public static final String CHANGE_EMAIL = BASE_URL + "/user/changeUsername";
    public static final String CHANGE_PASSWORD = BASE_URL + "/user/changePassword";
    public static final String CHANGE_PHONE_NO = BASE_URL + "/user/changePhone";
    public static final String DELETE_ACCOUNT = BASE_URL + "/user/deleteUser";
    public static final String GET_USERS_WHO_ACCEPTED_PROJECT = BASE_URL + "/project/accepted";
    public static final String ADD_REMOVE_FAVOURITE = BASE_URL + "/userData/setFavourite";

    public static final String CHANGE_PASSWORD_THROUGH_PHONE = BASE_URL + "/user/changePasswordPhone";


    public static final String FETCH_ALL_CHATROOMS = BASE_URL + "/chatroom/getChatroom";
    public static final String FETCH_FAVROURITE = BASE_URL + "/userData/getFavourites";
    public static final String PROJECT_AGREEMENT = BASE_URL + "/hire/addUser";
    public static final String FETCH_ONGOING_PROJECTS = BASE_URL + "/hire/workerHiredProjects";
    public static final String RECENTLY_WORKED = BASE_URL + "/hire/recentlyWorked" ;
    public static final String PROJECT_DONE = BASE_URL + "/hire/doneWorking";
    public static final String GET_WALLET_AMOUNT = BASE_URL + "/hire/getSumEarned";
    public static final String REPORT_ABUSE = BASE_URL + "/userData/report";
    public static final String FEEDBACK = BASE_URL + "/userData/feedback";
    public static final String CLOSE_PROJECT_BY_EMPLOYER = BASE_URL + "/project/completed";
    public static final String GET_FEEDBACK = BASE_URL + "/userdata/getFeedback";
    public static final String GET_PROFILE_PERCENT = BASE_URL + "/userData/getProfilePercent";
    public static final String CHANGE_PASSWORD_THROUGH_EMAIL = BASE_URL + "/user/changePasswordUsername";
    public static final String EMAIL_OTP = BASE_URL + "/userData/emailOtp";
    public static final String GET_APK_LINK = BASE_URL + "/app/getUploadedApp";


    public static ArrayList<CategoryModel> data = new ArrayList<CategoryModel>();
    public static List<String> categoryList()
    {
        List<String> catList = new ArrayList<>();
        for(CategoryModel model : data)
            catList.add(model.getName());

        return catList;
    }
}
