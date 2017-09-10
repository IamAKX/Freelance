package com.akash.applications.freelance;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import CustomJSONParser.ServerReply;
import LocalPrefrences.SessionData;
import Utils.Constants;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Connector.ConnectionDetector;
import dmax.dialog.SpotsDialog;

public class ResetPassword extends AppCompatActivity {

    TextView userInfo;
    MaterialEditText newPassword, confPassword;
    Button resetPasswordButton;
    CheckBox checkBox;
    Context context;
    ConnectionDetector detector;

    String Phone = "", Email = "", requestType ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setTitle("Reset Password");

        userInfo = (TextView) findViewById(R.id.userInfo);
        newPassword = (MaterialEditText) findViewById(R.id.resetNewPassword);
        confPassword = (MaterialEditText) findViewById(R.id.resetConfirmPassword);
        resetPasswordButton = (Button) findViewById(R.id.resetPasswordButton);
        checkBox = (CheckBox) findViewById(R.id.agreeChkBox);
        context = this;

        detector = new ConnectionDetector(context);

        if(getIntent().getExtras() != null)
        {


            if(getIntent().hasExtra("phoneNumber"))
            {
                Phone = getIntent().getStringExtra("phoneNumber");
                Log.e("checking",Phone);
                userInfo.setText("Phone : "+getIntent().getStringExtra("phoneNumber"));
                checkBox.setText("This is my Phone Number");
                requestType = "phone";
            }
            else if(getIntent().hasExtra("email")){
//                Uri data = getIntent().getData();
//                List<String> params = data.getPathSegments();
//                userInfo.setText("Email : "+params.get(0));
//                checkBox.setText("This is my Phone Email ID");
//                for(String s:params)
//                    Log.e("checking",s);
                Email = getIntent().getStringExtra("email");
                Log.e("checking",Email);
                userInfo.setText("Email : "+getIntent().getStringExtra("email"));
                checkBox.setText("This is my Email Address");
                requestType = "email";
            }
        }

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked())
                {
                    newPassword.setEnabled(true);
                    confPassword.setEnabled(true);
                    resetPasswordButton.setEnabled(true);
                    resetPasswordButton.setBackgroundResource(R.drawable.active_button_background);
                    if(newPassword.getText().toString().length() == 0 || confPassword.getText().toString().length() == 0)
                    {
                        resetPasswordButton.setEnabled(false);
                        resetPasswordButton.setBackgroundResource(R.drawable.inactive_button_background);
                    }
                }
                else
                {
                    newPassword.setEnabled(false);
                    confPassword.setEnabled(false);
                    resetPasswordButton.setEnabled(false);
                    resetPasswordButton.setBackgroundResource(R.drawable.inactive_button_background);
                }
            }
        });

        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(newPassword.getText().toString().length() == 0 || confPassword.getText().toString().length() == 0)
                {
                    resetPasswordButton.setEnabled(false);
                    resetPasswordButton.setBackgroundResource(R.drawable.inactive_button_background);
                }

                validateNewPassword(s.toString());

                if(confPassword.getText().toString().equals(newPassword.getText().toString()))
                {
                    resetPasswordButton.setEnabled(true);
                    resetPasswordButton.setBackgroundResource(R.drawable.active_button_background);
                    confPassword.setHelperText("Confirmed!");
                }
                else
                {
                    resetPasswordButton.setEnabled(false);
                    resetPasswordButton.setBackgroundResource(R.drawable.inactive_button_background);
                    confPassword.setHelperText("Same password as above");
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(newPassword.getText().toString().length() == 0 || confPassword.getText().toString().length() == 0)
                {
                    resetPasswordButton.setEnabled(false);
                    resetPasswordButton.setBackgroundResource(R.drawable.inactive_button_background);
                }

                if(confPassword.getText().toString().equals(newPassword.getText().toString()))
                {
                    resetPasswordButton.setEnabled(true);
                    resetPasswordButton.setBackgroundResource(R.drawable.active_button_background);
                    confPassword.setHelperText("Confirmed!");
                }
                else
                {
                    resetPasswordButton.setEnabled(false);
                    resetPasswordButton.setBackgroundResource(R.drawable.inactive_button_background);
                    confPassword.setHelperText("Same password as above");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final ConnectionDetector detector = new ConnectionDetector(context);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(detector.isConnectingToInternet()) {
                    Log.e("checking",requestType);
                    if (requestType.equalsIgnoreCase("email"))
                        new ResetUsingEmail().execute();
                    else
                        new ResetUsingPhoneNumber().execute();
                }
                else
                    detector.showSnackBar(v,"You lack Internet Connection");
            }
        });
    }

    private boolean validateNewPassword(String s) {
        boolean isValid = false;

        if(s.equals(""))
        {
            isValid = false;
            newPassword.setHelperText("Password must contain atlest 8 character with an upper case, lower case and a digit.");
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
                newPassword.setHelperText("Password looks good!");
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
        newPassword.setHelperText(errorMessage);
        return isValid;
    }

    class ResetUsingPhoneNumber extends AsyncTask<String,String,String>
    {

        SpotsDialog dialog = new SpotsDialog(context,"Resetting your password");
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.CHANGE_PASSWORD_THROUGH_PHONE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " ");
                            if(new ServerReply(response).getStatus())
                            {
                                Toast.makeText(context,"Password chnged, Log In again!",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(context,SignIn.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                            dialog.dismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                            dialog.dismiss();
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
                    //phone,oldPassword,newPassword
                    params.put("phone","+91"+Phone);
                    params.put("password",confPassword.getText().toString());
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

    class ResetUsingEmail extends AsyncTask<String,String,String>
    {

        SpotsDialog dialog = new SpotsDialog(context,"Resetting your password");
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.CHANGE_PASSWORD_THROUGH_EMAIL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " ");
                            if(new ServerReply(response).getStatus())
                            {
                                Toast.makeText(context,"Password chnged, Log In again!",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(context,SignIn.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                            dialog.dismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                            dialog.dismiss();
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
                    params.put("username",Email);
                    params.put("password",confPassword.getText().toString());
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
