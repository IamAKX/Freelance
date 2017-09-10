package com.akash.applications.freelance;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.mukesh.OtpView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import Connector.ConnectionDetector;
import CustomJSONParser.ServerReply;
import LocalPrefrences.OTPSessionHandler;
import Utils.Constants;
import dmax.dialog.SpotsDialog;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.fragments.BackConfirmationFragment;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;
import swarajsaaj.smscodereader.interfaces.OTPListener;
import swarajsaaj.smscodereader.receivers.OtpReader;

public class Register extends AppCompatActivity implements VerticalStepperForm, OTPListener{

    private VerticalStepperFormLayout verticalStepperForm;
    EditText fullNameET,passwordET,emailET,phoneET;
    OtpView otpView;
    private Context context;
    private String _OTPCODE = "xxxx";
    private boolean _OTPVerified = false;
    private boolean _OTPSession = false;
    private boolean _PhoneVerified = false;
    Button resendOTPBtn;
    ConnectionDetector detector;
    OTPTimerController timerController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = this;
        detector = new ConnectionDetector(context);
        int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        int colorPrimaryDark = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
        String[] stepsTitles = {"Full Name","Email","Password","Phone Number","OTP Verification"};
        String[] stepsSubtitles = {"People will search you with this name","Use this as your Login user ID","Password for login","Mobile Verification","Enter the OTP if not verified automatically"};
        // Here we find and initialize the form
        verticalStepperForm = (VerticalStepperFormLayout) findViewById(R.id.vertical_stepper_form);
        VerticalStepperFormLayout.Builder.newInstance(verticalStepperForm, stepsTitles, this, this)
                .stepsSubtitles(stepsSubtitles)
                //.materialDesignInDisabledSteps(true) // false by default
                //.showVerticalLineWhenStepsAreCollapsed(true) // false by default
                .primaryColor(colorPrimary)
                .primaryDarkColor(colorPrimaryDark)
                .displayBottomNavigation(true)
                .init();

        //Binding OTP reader with a specific sender's substring or constants like 9804945122,
        // leaving it blank will read all incomming messges

        OtpReader.bind(this,"NOTICE");
        timerController = new OTPTimerController((1*60*1000),1000);
    }

    @Override
    public View createStepContentView(int stepNumber) {
        View view = null;
        switch (stepNumber) {
            case 0:
                view = createFullNameTitleStep();
                break;
            case 1:
                view = createEmailStep();
                break;
            case 2:
                view = createPasswordStep();
                break;
            case 3:
                view = createPhoneStep();
                break;
            case 4:
                view = createOTPStep();
                break;
        }
        return view;
    }

    //----------------Username Step creation starts---------------
    private View createFullNameTitleStep() {
        fullNameET = new EditText(this);
        fullNameET.setSingleLine(true);
        fullNameET.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        fullNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateFullName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        fullNameET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(validateFullName(v.getText().toString()))
                    verticalStepperForm.goToNextStep();
                return false;
            }
        });
        return fullNameET;
    }

    private boolean validateFullName(String s) {
        boolean isValid = false;
        if(s.equals(""))
        {
            isValid = false;
            verticalStepperForm.setActiveStepAsUncompleted();
        }
        else
        {
            isValid=true;
            verticalStepperForm.setActiveStepAsCompleted();
        }
        return isValid;
    }

    //----------------Fullname Step creation ends---------------

    //----------------Password Step creation starts---------------
    private View createPasswordStep() {
        passwordET = new EditText(this);
        passwordET.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        passwordET.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePassword(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passwordET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(validatePassword(v.getText().toString()))
                    verticalStepperForm.goToNextStep();
                return false;
            }
        });
        return passwordET;
    }

    private boolean validatePassword(String s) {
        boolean isValid = false;

        if(s.equals(""))
        {
            isValid = false;
            verticalStepperForm.setActiveStepAsUncompleted();
            return isValid;
        }

        int NO_LOWER_CASE_STATUS = 0;
        int NO_UPPER_CASE_STATUS = 0;
        int NO_DIGIT_STATUS = 0;

        /* Error Index Code
            0 - Lower Case
            1 - Upper case
            2 - Digit
            3 - Password Length
         */

        char[] ErrorCode = {'1','1','1','1'};

        for(char c : s.toCharArray())
        {
            if((c >= 48 && c <=57) && NO_DIGIT_STATUS==0)
            {
                NO_DIGIT_STATUS = 1;
                ErrorCode[2] = '0';
            }
            if((c >= 'A' && c <='Z') && NO_UPPER_CASE_STATUS==0)
            {
                NO_UPPER_CASE_STATUS = 1;
                ErrorCode[1] = '0';
            }
            if((c >= 'a' && c <='z') && NO_LOWER_CASE_STATUS==0)
            {
                NO_LOWER_CASE_STATUS = 1;
                ErrorCode[0] = '0';
            }

        }
        if(s.length()>=8)
            ErrorCode[3] = '0';

        String errorMessage = "Password must ";
        switch (new String(ErrorCode))
        {
            case "0000":
                isValid = true;
                verticalStepperForm.setActiveStepAsCompleted();
                return isValid;

            case "1111":
                isValid = false;
                errorMessage += "contain atlest 8 character with an upper case, lower case and a digit.";
                break;

            case "0111":
                isValid = false;
                errorMessage += "contain atlest 8 character with an upper case and a digit.";
                break;

            case "1011":
                isValid = false;
                errorMessage += "contain atlest 8 character with a lower case and a digit.";
                break;

            case "1101":
                isValid = false;
                errorMessage += "contain atlest 8 character with an upper case and lower case.";
                break;

            case "1110":
                isValid = false;
                errorMessage += "contain an upper case, lower case and a digit.";
                break;

            case "0011":
                isValid = false;
                errorMessage += "contain atlest 8 character with a digit.";
                break;

            case "1001":
                isValid = false;
                errorMessage += "contain atlest 8 character with a lower case.";
                break;

            case "1100":
                isValid = false;
                errorMessage += "contain an upper case and a lower case.";
                break;

            case "0110":
                isValid = false;
                errorMessage += "contain an upper case and a digit.";
                break;

            case "0001":
                isValid = false;
                errorMessage += "contain atlest 8 character.";
                break;

            case "1000":
                isValid = false;
                errorMessage += "contain a lower case.";
                break;

            case "0100":
                isValid = false;
                errorMessage += "contain an upper case.";
                break;

            case "0010":
                isValid = false;
                errorMessage += "contain a digit.";
                break;
        }
        verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
        return isValid;
    }

    //--------Password step creation ends--------------------

    //--------Email step creation starts--------------------
    private View createEmailStep() {
        emailET = new EditText(this);
        emailET.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        emailET.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
        emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        emailET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(validateEmail(v.getText().toString()))
                    verticalStepperForm.goToNextStep();
                return false;
            }
        });
        return emailET;
    }

    private boolean validateEmail(String s) {
        boolean isValid = false;

        Pattern pattern;
        Matcher matcher;

        if(s.equals(""))
        {
            verticalStepperForm.setActiveStepAsUncompleted();
            return isValid;
        }

        pattern = Pattern.compile(Constants.EMAIL_PATTERN);
        matcher = pattern.matcher(s);
        isValid  = matcher.matches();

        if(isValid)
            verticalStepperForm.setActiveStepAsCompleted();
        else
            verticalStepperForm.setActiveStepAsUncompleted("Enter a valid email");
        return isValid;
    }

    private void verifyEmail(String email)
    {
        if(!detector.isConnectingToInternet())
        {
            detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");
            return;
        }
        phoneET.setEnabled(false);
        new VerifyEmail(email).execute();
    }
    //--------Phone step creation starts--------------------
    private View createPhoneStep() {
        phoneET = new EditText(this);
        phoneET.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        phoneET.setInputType(InputType.TYPE_CLASS_PHONE);
        phoneET.setText("+91");
        phoneET.setSelection(phoneET.getText().length());
        phoneET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePhone(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phoneET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(validatePhone(v.getText().toString()))
                    verticalStepperForm.goToNextStep();
                return false;
            }
        });
        return phoneET;
    }

    private boolean validatePhone(String s) {
        verticalStepperForm.setActiveStepAsUncompleted();
        boolean isValid = false;
        if(s.charAt(0)!='+')
        {
            verticalStepperForm.setActiveStepAsUncompleted("Enter your country code in begining.");
            return isValid;
        }
        for(char c : s.substring(1).toCharArray())
        {
            if((c < 48 || c > 57) && c!='+')
            {
                isValid = false;
                verticalStepperForm.setActiveStepAsUncompleted("Enter valid phone number.");
                return isValid;
            }
        }

        if(s.length()>3)
        {

            if(s.substring(0,3).equalsIgnoreCase("+91") && s.length()==13)
            {
                isValid = true;
                //verticalStepperForm.setActiveStepAsCompleted();
            }
        }
        if(isValid)
            verticalStepperForm.setActiveStepAsCompleted();
        else
            verticalStepperForm.setActiveStepAsUncompleted();
        return isValid;
    }

    //--------OTP step creation starts--------------------
    private View createOTPStep() {
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        LinearLayout otpLayout =
                (LinearLayout) inflater.inflate(R.layout.otp_verification, null, false);
        otpView = (OtpView) otpLayout.findViewById(R.id.otp_view);
        otpView.setOTP("");
        resendOTPBtn = (Button) otpLayout.findViewById(R.id.otpResendBtn);
        resendOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOTPBtn.setEnabled(false);
                resendOTPBtn.setTextColor(context.getResources().getColor(R.color.lighterGray));
                resendOTPBtn.setText("RESEND OTP IN 1:00");
                requestForOTP();
                Toast.makeText(context,"Request for resending OTP is accepted",Toast.LENGTH_SHORT).show();

            }
        });
        return otpLayout;
    }



    @Override
    public void onStepOpening(int stepNumber) {
        switch (stepNumber) {
            case 0:
                validateFullName(fullNameET.getText().toString());
                break;
            case 1:
                validateEmail(emailET.getText().toString());
                break;
            case 2:
                verifyEmail(emailET.getText().toString());
                validatePassword(passwordET.getText().toString());
                break;
            case 3:
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(Register.this, new String[] { Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.CALL_PHONE },Constants.SMS_GROUP_CODE);
                }
                validatePhone(phoneET.getText().toString());
                break;
            case  4:
                if(!_PhoneVerified)
                {
                    if(!detector.isConnectingToInternet())
                    {
                        detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");
                        return;
                    }

                    new VerifyPhoneNumber(phoneET.getText().toString()).execute();
                }
                break;
            case 5:

                if(_OTPCODE.equals("xxxx"))
                    _OTPCODE = otpView.getOTP();
                if(!_OTPVerified)
                {
                    _OTPVerified = true;
                    new ValidateOTP(_OTPCODE).execute();
                }
                break;
        }
    }

    @Override
    public void sendData() {

        if(!detector.isConnectingToInternet())
        {
            detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");
            return;
        }

        new UserRegister().execute();

    }

    @Override
    public void otpReceived(String messageText) {

        //Send OTP message in this format
        //Your Frelance mobile verification OTP is 1547.

        messageText=messageText.substring(messageText.indexOf("is"));
        messageText=messageText.substring(messageText.indexOf(" ")+1,messageText.indexOf(".")).trim();
        Log.i("Checking","OTP : "+messageText);
        if(_OTPCODE.equals("xxxx"))
            _OTPCODE=messageText;
        if(!_OTPVerified)
        {
            if(!detector.isConnectingToInternet())
            {
                detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");
                return;
            }

            new ValidateOTP(_OTPCODE).execute();
            _OTPVerified = true;
        }
    }


    private class VerifyEmail extends AsyncTask<String,String,String>{
        String email;
        SpotsDialog spotsDialog;
        public VerifyEmail(String email) {
            this.email = email;
            this.spotsDialog = new SpotsDialog(context,"Verifing your email");
        }

        @Override
        protected String doInBackground(String... params) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.VERIFY_EMAIL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " ");
                            if(response.equalsIgnoreCase("true"))
                            {
                                spotsDialog.dismiss();
                                phoneET.setEnabled(true);

                            }
                            else
                            {
                                spotsDialog.dismiss();
                                verticalStepperForm.setActiveStepAsUncompleted();
                                verticalStepperForm.goToPreviousStep();
                                verticalStepperForm.setActiveStepAsUncompleted("This email is already registered.");
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                            NetworkResponse networkResponse = error.networkResponse;
                            if(networkResponse != null && networkResponse.data != null)
                            {
                                switch (networkResponse.statusCode)
                                {
                                    case 500:
                                        spotsDialog.dismiss();
                                        verticalStepperForm.setActiveStepAsUncompleted();
                                        verticalStepperForm.goToPreviousStep();
                                        verticalStepperForm.setActiveStepAsUncompleted("This email is already registered.");
                                        break;
                                    default:
                                        spotsDialog.dismiss();
                                        verticalStepperForm.setActiveStepAsUncompleted();
                                        verticalStepperForm.goToPreviousStep();
                                        verticalStepperForm.setActiveStepAsUncompleted("There was some server error. Please re-verify.");
                                        break;
                                }
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request
                    params.put("username", email);
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

    @Override
    public void onBackPressed() {
        if(verticalStepperForm.isAnyStepCompleted()) {
            BackConfirmationFragment backConfirmation = new BackConfirmationFragment();
            backConfirmation.setOnConfirmBack(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            backConfirmation.setOnNotConfirmBack(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    finish();
                }
            });
            backConfirmation.show(getSupportFragmentManager(), null);
        } else {

            finish();
        }
    }

    private class UserRegister extends AsyncTask<String,String,String> {

        SpotsDialog spotsDialog;

        public UserRegister() {
            spotsDialog = new SpotsDialog(context, "Please wait...");
        }

        @Override
        protected String doInBackground(String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.REGISTER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " ");
                            if(new ServerReply(response).getStatus())
                            {
                                spotsDialog.dismiss();
                                showValidateEmailPrompt();
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
                    params.put("name", fullNameET.getText().toString());
                    params.put("username", emailET.getText().toString());
                    params.put("password", passwordET.getText().toString());
                    params.put("phoneNo", phoneET.getText().toString());
                    Log.i("checking",fullNameET.getText().toString()+"\n"+emailET.getText().toString()+"\n"+passwordET.getText().toString()+""+phoneET.getText().toString());
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spotsDialog.show();
        }
    }

    private void showValidateEmailPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("Verification link is sent to your email. Do you want to check it?");
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
                Toast.makeText(context,"Registered successfully, please verify your email to login..." ,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, SignIn.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class ValidateOTP extends AsyncTask<String,String,String>{
        String otp;
        SpotsDialog spotsDialog;
        public ValidateOTP(String otp) {
            _OTPVerified = false;
            this.otp = otp;
            spotsDialog = new SpotsDialog(context, "Verifying your OTP...");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equalsIgnoreCase("true"))
            {
                spotsDialog.dismiss();
                verticalStepperForm.goToNextStep();
            }
            else
            {
                spotsDialog.dismiss();
                verticalStepperForm.setActiveStepAsCompleted();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Invalid OTP");
                builder.setMessage("The otp you have enter is not valid. What would you like to do?");
                builder.setPositiveButton("Re-send OTP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        timerController.onFinish();
                        resendOTPBtn.setEnabled(false);
                        resendOTPBtn.setTextColor(context.getResources().getColor(R.color.lighterGray));
                        resendOTPBtn.setText("RESEND OTP IN 1:00");
                        _OTPSession = false;
                        new OTPSessionHandler(context).removeOtpNumber();
                        requestForOTP();

                        _OTPCODE="xxxx";
                        _OTPVerified=false;
                        if(verticalStepperForm.getActiveStepNumber()!=4)
                            verticalStepperForm.setActiveStepAsUncompleted();
                        verticalStepperForm.goToStep(4,false);
                    }
                });
                builder.setNegativeButton("Re-enter OTP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        _OTPCODE="xxxx";
                        _OTPVerified=false;
                        if(verticalStepperForm.getActiveStepNumber()!=4)
                            verticalStepperForm.setActiveStepAsUncompleted();
                        verticalStepperForm.goToStep(4,false);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case Constants.SMS_GROUP_CODE:
                    if (!(grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED
                            || grantResults[1] == PackageManager.PERMISSION_GRANTED || grantResults[2] == PackageManager.PERMISSION_GRANTED))) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("You have denied Reading SMS Permission. ");
                        builder.setMessage("The OTP will not be verified automatically. Do you want to continue?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
                                {
                                    ActivityCompat.requestPermissions(Register.this, new String[] { Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS },Constants.SMS_GROUP_CODE);
                                }
                            }
                        });
                        builder.setCancelable(false);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }


        }

    }

    private class VerifyPhoneNumber extends AsyncTask<String,String,String> {
        String phoneNumber;
        SpotsDialog spotsDialog;

        public VerifyPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            spotsDialog = new SpotsDialog(context, "Verifying your Phone Number...");
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
                                _PhoneVerified = true;
                                verticalStepperForm.setActiveStepAsCompleted();
                                //Calling OTP request
                                requestForOTP();

                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                            NetworkResponse networkResponse = error.networkResponse;
                            if(networkResponse != null && networkResponse.data != null)
                            {
                                switch (networkResponse.statusCode)
                                {
                                    case 500:
                                        spotsDialog.dismiss();
                                        verticalStepperForm.setActiveStepAsUncompleted();
                                        verticalStepperForm.goToPreviousStep();
                                        verticalStepperForm.setActiveStepAsUncompleted("This Phone number is already linked with an account. Try to recover the account or enter another phone number");
                                        break;
                                    default:
                                        spotsDialog.dismiss();
                                        verticalStepperForm.setActiveStepAsUncompleted();
                                        verticalStepperForm.goToPreviousStep();
                                        verticalStepperForm.setActiveStepAsUncompleted("There was some server error. Please re-verify.");
                                        break;
                                }
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request
                    params.put("phoneNo", phoneET.getText().toString());
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


    //--------------------------DO NOT DELETE COMMENTED LINES BELOW, REUSE IN FUTURE-----------------

//
//    public String requestForOTP()
//    {
//        if(!_OTPSession)
//            timerController.start();
//        Toast.makeText(context,"I am in requestOTP()",Toast.LENGTH_SHORT).show();
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SEND_OTP,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        //If we are getting success from server
//                        Toast.makeText(context,"OTP sent status : " + new ServerReply(response).getStatus(),Toast.LENGTH_SHORT).show();
//
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        //You can handle error here if you want
//                        Log.i("Checking", error + " ");
//                        Toast.makeText(context,error.toString(),Toast.LENGTH_LONG).show();
//                    }
//                }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                //Adding parameters to request
//                params.put("phoneNo", phoneET.getText().toString());
//
//                return params;
//            }
//        };
//
//        //Adding the string request to the queue
//        stringRequest.setShouldCache(false);
//
//        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        requestQueue.getCache().clear();
//        if(!_OTPSession)
//        {
//            Log.i("Checking","Adding to queue");
//            requestQueue.add(stringRequest);
//            _OTPSession = true;
//        }
//        return null;
//    }


    public String requestForOTP()
    {
        String _OtpNumber = String.format("%04d", new Random().nextInt(10000));
        new OTPSessionHandler(context).setOtpNumber(_OtpNumber);

        final String composedOtpMessage = "Your Frelance mobile verification OTP is "+_OtpNumber+". \n\nThis OTP will expire within 1 minutes.";
        if(!_OTPSession)
        {
            timerController.start();
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
                params.put("phoneNumber", phoneET.getText().toString());
                params.put("message",composedOtpMessage);
                params.put("accessKey",Constants.SMS_ANY_API);
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



    //-------------------------Re-send OTP Timer Controller------------------------

    private class OTPTimerController extends CountDownTimer {

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

            resendOTPBtn.setText("RESEND OTP IN "+min_sec);
        }

        @Override
        public void onFinish() {
            new OTPSessionHandler(context).removeOtpNumber();
            resendOTPBtn.setEnabled(true);
            _OTPSession = false;
            resendOTPBtn.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            resendOTPBtn.setText("RESEND OTP");
        }
    }
}
