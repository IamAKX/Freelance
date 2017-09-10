package com.akash.applications.freelance;

import android.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.mukesh.OtpView;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import Connector.ConnectionDetector;
import CustomJSONParser.ModeParser;
import CustomJSONParser.ServerReply;
import CustomJSONParser.SignInParser;
import DataModel.CategoryModel;
import LocalPrefrences.EmailOTPHandler;
import LocalPrefrences.OTPSessionHandler;
import LocalPrefrences.PreferenceKey;
import LocalPrefrences.SessionData;
import LocalPrefrences.UserDetails;
import Utils.Constants;
import dmax.dialog.SpotsDialog;
import swarajsaaj.smscodereader.interfaces.OTPListener;
import swarajsaaj.smscodereader.receivers.OtpReader;

public class SignIn extends AppCompatActivity implements OTPListener{

    MaterialEditText userID, password, resetPassPhone, resetPassEmail,reverifyEmail;
    Button btnLogin, recoveryPhone, recoveryEmail, smsDone, emailDone, resendOTP, verifyOTP, verifyEmailOTP, reverifyEmailDone, resendEmail;
    EditText emailOTPEdit;
    Context context;
    OtpView otpView;
    ExpandableRelativeLayout smsExpandableLayout, emailExpandableLayout, reverifyExpandableLayout;
    boolean isSMSChildOpen = false;
    OTPTimerController otpTimerController;
    private InputMethodManager inputMethodManager;
    ConnectionDetector detector;
    private String _OTPCODE = "xxxx";
    private boolean _OTPSession = false;
    private boolean _OTPVerified = false;
    LinearLayout emailOTP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = this;
        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        detector = new ConnectionDetector(context);
        
        userID  = (MaterialEditText) findViewById(R.id.signInEmail);
        password = (MaterialEditText) findViewById(R.id.signInPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        recoveryPhone = (Button) findViewById(R.id.sms_prompt_btn);
        recoveryEmail = (Button) findViewById(R.id.email_prompt_btn);
        smsDone = (Button) findViewById(R.id.sms_done);
        emailDone = (Button) findViewById(R.id.email_done);
        otpView = (OtpView) findViewById(R.id.otp_view_reset_password);
        resendOTP = (Button) findViewById(R.id.resendOTP);
        verifyOTP = (Button) findViewById(R.id.verifyOTP);
        resetPassPhone = (MaterialEditText) findViewById(R.id.resetpass_Phone);
        resetPassEmail = (MaterialEditText) findViewById(R.id.resetpass_Email);
        smsExpandableLayout = (ExpandableRelativeLayout) findViewById(R.id.expandableLayoutSMS);
        emailExpandableLayout = (ExpandableRelativeLayout) findViewById(R.id.expandableLayoutEmail);
        reverifyExpandableLayout = (ExpandableRelativeLayout) findViewById(R.id.expandableEmailVerificationLink);
        reverifyEmail = (MaterialEditText) findViewById(R.id.reverify_Email);
        reverifyEmailDone = (Button) findViewById(R.id.revrifyemail_done);
        resendEmail = (Button) findViewById(R.id.resend_email_link_btn);


        emailOTP = (LinearLayout) findViewById(R.id.resetThroughEmailLL);
        verifyEmailOTP = (Button) findViewById(R.id.verify_email_otp);
        emailOTPEdit = (EditText) findViewById(R.id.emailOTP);
        emailOTPEdit.setEnabled(false);
        
        checkIntentData();
        if(detector.isConnectingToInternet() && !new UserDetails(context).getUserEmail().equalsIgnoreCase(PreferenceKey.NO_EMAIL_FOUND))
            fetchCategory();


        UserDetails details = new UserDetails(context);
        if(!details.getUserEmail().equalsIgnoreCase(PreferenceKey.NO_EMAIL_FOUND))
            userID.setText(details.getUserEmail().trim());

        otpTimerController = new OTPTimerController((1*60*1000),1000);

        recoveryPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSMSChildOpen)
                {
                    smsExpandableLayout.moveChild(1);
                    isSMSChildOpen = true;
                }
                else
                {
                    smsExpandableLayout.collapse();
                    isSMSChildOpen = false;
                }

            }
        });

        recoveryEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailExpandableLayout.toggle();

            }
        });
        smsDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
                {
                ActivityCompat.requestPermissions(SignIn.this, new String[] { android.Manifest.permission.READ_SMS, android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.CALL_PHONE },Constants.SMS_GROUP_CODE);
                }
                else {
                    if (!detector.isConnectingToInternet()) {
                        detector.showSnackBar(v, "You lack Internet Connectivity");
                        return;
                    }

                    new VerifyPhone().execute();
                }
            }
        });

        emailDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!detector.isConnectingToInternet())
                {
                    detector.showSnackBar(v,"You lack Internet Connectivity");
                    return;
                }

               new SendEmailOTP(resetPassEmail.getText().toString()).execute();
            }
        });

        verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new VerifyOTP(otpView.getOTP()).execute();
            }
        });
        userID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(validateUserID() && validatePassword())
                    loginButtonController(true);
                else
                    loginButtonController(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(validateUserID() && validatePassword())
                    loginButtonController(true);
                else
                    loginButtonController(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Request for logging
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                if(!detector.isConnectingToInternet())
                {
                    detector.showSnackBar(v,"You lack Internet Connectivity");
                    return;
                }

                new LogIn().execute();
            }
        });

        resetPassPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() == 10)
                {
                    smsDone.setEnabled(true);
                    smsDone.setBackgroundResource(R.drawable.active_button_background);
                }
                else
                {
                    smsDone.setEnabled(false);
                    smsDone.setBackgroundResource(R.drawable.inactive_button_background);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        resetPassEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()>0)
                {
                    emailDone.setEnabled(true);
                    emailDone.setBackgroundResource(R.drawable.active_button_background);
                }
                else
                {
                    emailDone.setEnabled(false);
                    emailDone.setBackgroundResource(R.drawable.inactive_button_background);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        reverifyEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()>0)
                {
                    reverifyEmailDone.setEnabled(true);
                    reverifyEmailDone.setBackgroundResource(R.drawable.active_button_background);
                }
                else
                {
                    reverifyEmailDone.setEnabled(false);
                    reverifyEmailDone.setBackgroundResource(R.drawable.inactive_button_background);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOTP.setEnabled(false);
                resendOTP.setTextColor(context.getResources().getColor(R.color.lighterGray));
                resendOTP.setText("RESEND OTP IN 1:00");
                //Resend OTP Request
                requestForOTP();
                Toast.makeText(context,"Request for resending OTP is accepted",Toast.LENGTH_SHORT).show();
                otpTimerController.start();
            }
        });

        resendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reverifyExpandableLayout.toggle();
            }
        });

        reverifyEmailDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(!detector.isConnectingToInternet())
                {
                    detector.showSnackBar(v,"You lack Internet Connectivity");
                    return;
                }

                new ReEmailSendVerifyLink(reverifyEmail.getText().toString()).execute();
            }
        });


        verifyEmailOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailOTPEdit.getText().toString().equalsIgnoreCase(new EmailOTPHandler(context).getOtpNumber()))
                {
                    new EmailOTPHandler(context).removeOtpNumber();
                    startActivity(new Intent(context,ResetPassword.class).putExtra("email",resetPassEmail.getText().toString()));
                    finish();
                }
                else
                    emailOTPEdit.setError("OTP did not matched");
            }
        });
        OtpReader.bind(this,"NOTICE");
    }

    private void checkIntentData() {
        if(getIntent().getExtras() != null)
        {
            if(!detector.isConnectingToInternet())
            {
                detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");
                return;
            }

            Uri data = getIntent().getData();
            String token = data.toString();
            Log.e("checking",token.substring(token.indexOf('=')+1));
            new SendEmailVerificationToken(token).execute();

        }
    }

    private boolean validateUserID() {
        boolean isValid = false;
        if(userID.getText().toString().length()>0)
            isValid = true;
        return isValid;
    }

    private boolean validatePassword() {
        boolean isValid = false;
        if(password.getText().toString().length()>0)
            isValid = true;
        return isValid;
    }

    private void loginButtonController(boolean activate)
    {
        if(activate)
        {
            btnLogin.setEnabled(activate);
            btnLogin.setBackgroundResource(R.drawable.active_button_background);
        }
        else
        {
            btnLogin.setEnabled(activate);
            btnLogin.setBackgroundResource(R.drawable.inactive_button_background);
        }
    }

    @Override
    public void otpReceived(String messageText) {
        Log.e("checking","I am in otpReceive   "+messageText);
        messageText=messageText.substring(messageText.indexOf("is"));
        messageText=messageText.substring(messageText.indexOf(" ")+1,messageText.indexOf(".")).trim();
        Log.i("Checking","OTP : "+messageText);
        if(_OTPCODE.equals("xxxx"))
            _OTPCODE=messageText;

        if(!_OTPVerified)
        {
            new VerifyOTP(_OTPCODE).execute();
            _OTPVerified = true;
            Log.e("checking",_OTPCODE+"  "+_OTPVerified);
        }
    }

    private class LogIn extends AsyncTask<String,String,String>{
        SpotsDialog spotsDialog;

        public LogIn() {
            spotsDialog = new SpotsDialog(context,"Signing In, please wait");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spotsDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LOGIN,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking..", response + " ");
                            if(new ServerReply(response).getStatus())
                            {

                                Log.e("checking"," res "+ response );
                                spotsDialog.dismiss();
                                new SessionData(context).setSessionKey(new ServerReply(response).getToken());
                                UserDetails details = new UserDetails(context);
                                details.setUserName(new SignInParser(response).getName());
                                details.setUserEmail(new SignInParser(response).getUserEmail());
                                details.setUserCategory(new SignInParser(response).getCategory());
                                details.setUserImage(Constants.USER_CURRENT_PROFILE_IMAGE + new SignInParser(response).getImage());
                                getUserMode();

                                Intent intent = new Intent(context, Home.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                if(details.getUserCategory().equalsIgnoreCase(PreferenceKey.NO_KEY_FOUND))
                                    intent.putExtra("open","category");
                                startActivity(intent);

                                finish();
                            }
                            else
                            {
                                spotsDialog.dismiss();
                                String errorMessage = new ServerReply(response).getReason();
                                userID.setError(errorMessage);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want

                            String errorMessage = null;
                            NetworkResponse networkResponse = error.networkResponse;
//                            errorMessage = new String(error.networkResponse.data);
//                            Log.e("checking",errorMessage+" ");

                            if(networkResponse != null && networkResponse.data != null)
                            {
                                switch (networkResponse.statusCode)
                                {
                                    case 500:
                                        spotsDialog.dismiss();
                                        errorMessage = new ServerReply(new String(error.networkResponse.data)).getReason();
                                        userID.setError(errorMessage);
                                        break;
                                    default:
                                        spotsDialog.dismiss();
                                        errorMessage = new ServerReply(new String(error.networkResponse.data)).getReason();
                                        userID.setError(errorMessage);
                                        break;
                                }
                            }
                            spotsDialog.dismiss();
                            Toast.makeText(context,"Error : Server is unreachable",Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request
                    params.put("username",userID.getText().toString());
                    params.put("password",password.getText().toString());
                    return params;
                }
            };

            //Adding the string request to the queue
            stringRequest.setShouldCache(false);

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.getCache().clear();

            requestQueue.add(stringRequest);

            return null;
        }


    }

    private void getUserMode() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.GET_MODE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        ModeParser parser = new ModeParser(response);
                        if(parser.getStatus())
                            new UserDetails(context).setUserMode(parser.getMode());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        Log.e("Checking","error");
                        NetworkResponse networkResponse = error.networkResponse;
                        if(networkResponse != null && networkResponse.data != null)
                        {
                            switch (networkResponse.statusCode)
                            {
                                default:
                                    Toast.makeText(context,"Server interal error",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put("authorization", "Bearer "+ new SessionData(context).getSessionKey());
                return params;
            }
        };
        //Adding the string request to the queue
        stringRequest.setShouldCache(false);

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.getCache().clear();

        requestQueue.add(stringRequest);
    }

    private class VerifyPhone extends AsyncTask<String,String,String>{
        SpotsDialog spotsDialog;

        public VerifyPhone() {
            spotsDialog = new SpotsDialog(context,"Verifying Phone number");
        }

        @Override
        protected String doInBackground(String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.VERIFY_PHONE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " ");
                            spotsDialog.dismiss();
                            if(response.equalsIgnoreCase("true"))
                            {

                                //Calling OTP request

                                if(detector.isConnectingToInternet()) {
                                    requestForOTP();
                                    smsExpandableLayout.moveChild(0);
                                    otpTimerController.start();
                                }
                                else
                                    detector.showSnackBar(getWindow().getDecorView(),"You lack Internet connection");

                            }
                            spotsDialog.dismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                            spotsDialog.dismiss();
                            NetworkResponse networkResponse = error.networkResponse;
                            if(networkResponse != null && networkResponse.data != null)
                            {
                                switch (networkResponse.statusCode)
                                {
                                    case 500:
                                        spotsDialog.dismiss();
                                        break;
                                    default:
                                        spotsDialog.dismiss();
                                        break;
                                }
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request
                    params.put("phoneNo", resetPassPhone.getText().toString());
                    return params;
                }
            };

            //Adding the string request to the queue
            stringRequest.setShouldCache(false);

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.getCache().clear();

            requestQueue.add(stringRequest);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spotsDialog.show();
        }


    }

    private class VerifyOTP extends AsyncTask<String,String,String>{

        String otp;
        SpotsDialog spotsDialog;
        public VerifyOTP(String otp) {
            _OTPVerified = false;
            this.otp = otp;
            spotsDialog = new SpotsDialog(context, "Verifying your OTP...");

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.e("checking",s+" Post");

            if(s.equalsIgnoreCase("true"))
            {

                spotsDialog.dismiss();
                smsExpandableLayout.collapse();

                startActivity(new Intent(context,ResetPassword.class).putExtra("phoneNumber",resetPassPhone.getText().toString()));
                finish();
            }
            else
            {
                spotsDialog.dismiss();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Invalid OTP");
                builder.setMessage("The otp you have enter is not valid. What would you like to do?");
                builder.setPositiveButton("Re-send OTP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        otpTimerController.onFinish();
                        resendOTP.setEnabled(false);
                        resendOTP.setTextColor(context.getResources().getColor(R.color.lighterGray));
                        resendOTP.setText("RESEND OTP IN 1:00");
                        _OTPSession = false;
                        new OTPSessionHandler(context).removeOtpNumber();
                        requestForOTP();

                        _OTPCODE="xxxx";
                        _OTPVerified=false;

                    }
                });
                builder.setNegativeButton("Re-enter OTP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        _OTPCODE="xxxx";
                        _OTPVerified=false;

                    }
                });
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
                //Toast.makeText(context,networkResponse.data.toString(),Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {


            try {
                Thread.sleep(1500);
                Log.i("Checking",_OTPCODE+"\n"+new OTPSessionHandler(context).getOtpNumber());
                if(_OTPCODE.equals(new OTPSessionHandler(context).getOtpNumber()))
                    return "true";
                else
                    return "false";
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spotsDialog.show();
        }

    }

//    private class SendEmailVerification extends AsyncTask<String,String,String> {
//
//        SpotsDialog spotsDialog;
//
//        public SendEmailVerification() {
//            spotsDialog = new SpotsDialog(context,"Validating Email");
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.Email_Verification_URL,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            //If we are getting success from server
//                            Log.i("Checking", response + " ");
//                            spotsDialog.dismiss();
//                            if(response.equalsIgnoreCase("1"))
//                            {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//
//                                builder.setMessage("Verification link is sent to your email. Do you want to check it?");
//                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                        dialog.dismiss();
//                                        //Launch email client app
//                                        Intent intent = new Intent(Intent.ACTION_MAIN);
//                                        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
//                                        startActivity(intent);
//                                    }
//                                });
//                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//
//                                    }
//                                });
//                                builder.setCancelable(false);
//                                AlertDialog dialog = builder.create();
//                                dialog.show();
//                            }
//
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            //You can handle error here if you want
//
//                        }
//                    }) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<>();
//                    //Adding parameters to request
//                    params.put("email", resetPassEmail.getText().toString());
//                    return params;
//                }
//            };
//
//            //Adding the string request to the queue
//            stringRequest.setShouldCache(false);
//
//            RequestQueue requestQueue = Volley.newRequestQueue(context);
//            requestQueue.getCache().clear();
//
//            requestQueue.add(stringRequest);
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            spotsDialog.show();
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//
//        }
//    }


    //-------------------------Re-send OTP Timer Controller------------------------

    private class OTPTimerController extends CountDownTimer{

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public OTPTimerController(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            String min_sec = String.format("%01d:%02d", (TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))),
                    (TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));

            resendOTP.setText("RESEND OTP IN "+min_sec);
        }

        @Override
        public void onFinish() {
            resendOTP.setEnabled(true);
            resendOTP.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            resendOTP.setText("RESEND OTP");
        }
    }

    private class SendEmailVerificationToken extends AsyncTask<String,String,String>{

        String token;
        SpotsDialog spotsDialog;
        public SendEmailVerificationToken(String token) {
            spotsDialog = new SpotsDialog(SignIn.this,"Please wait while we verify you email...");
            this.token = token;
            spotsDialog.show();
            Toast.makeText(SignIn.this, "Please wait while we verify your email.." ,Toast.LENGTH_LONG).show();
        }



        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("checking in post exe",s+" ");
            spotsDialog.dismiss();

        }

        @Override
        protected String doInBackground(String... params) {

            StringRequest stringRequest = new StringRequest(Request.Method.GET, token,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server

                                Toast.makeText(SignIn.this,response,Toast.LENGTH_LONG).show();


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want

                            NetworkResponse networkResponse = error.networkResponse;
                            //Log.i("Checking", new String(error.networkResponse.data));
                            if(networkResponse != null && networkResponse.data != null)
                            {
                                switch (networkResponse.statusCode)
                                {
                                }
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request
                    params.put("token",token);
                    return params;
                }
            };

            //Adding the string request to the queue
            stringRequest.setShouldCache(false);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.getCache().clear();

            requestQueue.add(stringRequest);

            return null;
        }
    }

    private class ReEmailSendVerifyLink extends AsyncTask<String,String,String>{

        private final SpotsDialog spotsDialog;
        String emailToVerify;

        public ReEmailSendVerifyLink(String emailToVerify) {
            this.emailToVerify = emailToVerify;
            spotsDialog = new SpotsDialog(context, "Please wait...");
            spotsDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.RESEND_EMAIL_VERIFICATIONLINK,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " ");
                            if(new ServerReply(response).getStatus())
                            {
                                spotsDialog.dismiss();
                                showValidateEmailPrompt("Verification link is sent to your email. Do you want to check it?");
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want

                            NetworkResponse networkResponse = error.networkResponse;
                            //Log.i("Checking", new String(error.networkResponse.data));
                            if(networkResponse != null && networkResponse.data != null)
                            {
                                switch (networkResponse.statusCode)
                                {
                                    case 500:
                                        spotsDialog.dismiss();

                                        break;
                                    default:
                                        spotsDialog.dismiss();

                                        break;
                                }
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request
                    params.put("username",emailToVerify);
                    return params;
                }
            };

            //Adding the string request to the queue
            stringRequest.setShouldCache(false);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.getCache().clear();

            requestQueue.add(stringRequest);
            return null;
        }
    }

    private void showValidateEmailPrompt(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                //Launch email client app
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(context,"Verification link sent to the email..." ,Toast.LENGTH_LONG).show();


            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void fetchCategory() {
        if(!detector.isConnectingToInternet())
        {
            detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.FETCH_CATEGORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        Log.i("Checking", response + " ");
                        Constants.data.clear();
                        try {
                            JSONArray array = new JSONArray(response);
                            for(int i = 0; i< array.length(); i++)
                            {
                                JSONObject object = (JSONObject) array.get(i);
                                CategoryModel model = new CategoryModel(object.getString("name"),object.getString("url"));
                                Constants.data.add(model);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("checking",Constants.categoryList()+" ");

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want

                        NetworkResponse networkResponse = error.networkResponse;
                      //  Log.i("Checking", " "+new String(error.networkResponse.data));
                        if(networkResponse != null && networkResponse.data != null)
                        {
                            switch (networkResponse.statusCode)
                            {

                                default:
                                    break;
                            }
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                return params;
            }
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                return params;
            }
        };

        //Adding the string request to the queue
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.getCache().clear();

        requestQueue.add(stringRequest);

    }


    private String requestForOTP() {
        String _OtpNumber = String.format("%04d", new Random().nextInt(10000));
        new OTPSessionHandler(context).setOtpNumber(_OtpNumber);

        final String composedOtpMessage = "Your Frelance mobile verification OTP is "+_OtpNumber+". \n\nThis OTP will expire within 1 minutes.";
        if(!_OTPSession)
        {
            otpTimerController.start();
        }
        Log.i("Checking","I am in requestForOtp");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SMS_ANY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        Log.i("Checking", error + " ");

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put("phoneNumber", resetPassPhone.getText().toString());
                params.put("message",composedOtpMessage);
                params.put("accessKey", Constants.SMS_ANY_API);
                return params;
            }
        };

        //Adding the string request to the queue
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.getCache().clear();
        if(!_OTPSession)
        {
            Log.i("Checking","Adding to queue");
            requestQueue.add(stringRequest);
            _OTPSession = true;
        }
        return null;
    }

    private class SendEmailOTP extends AsyncTask<String,String,String>{

        String email;
        SpotsDialog spotsDialog = new SpotsDialog(context,"Requesting for OTP");

        public SendEmailOTP(String email) {
            this.email = email;
            spotsDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.EMAIL_OTP,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " ");
                            try {
                                JSONObject object = new JSONObject(response);
                                new EmailOTPHandler(context).setOtpNumber(object.getString("otp"));
                                emailOTPEdit.setEnabled(true);
                                showValidateEmailPrompt("One Time Password is sent to your email. Do you want to check it?");
                                verifyEmailOTP.setEnabled(true);
                                verifyEmailOTP.setBackgroundResource(R.drawable.active_button_background);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            spotsDialog.dismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                            spotsDialog.dismiss();
                            NetworkResponse networkResponse = error.networkResponse;
                            //Log.i("Checking", new String(error.networkResponse.data));
                            if(networkResponse != null && networkResponse.data != null)
                            {
                                switch (networkResponse.statusCode)
                                {

                                    default:
                                        break;
                                }
                            }
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    //params.put("authorization", "Bearer "+ new SessionData(context).getSessionKey());
                    return params;
                }
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request
                    params.put("username",email);
                    return params;
                }
            };

            //Adding the string request to the queue
            stringRequest.setShouldCache(false);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.getCache().clear();

            requestQueue.add(stringRequest);

            return null;
        }
    }
}
