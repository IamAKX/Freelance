package com.akash.applications.freelance;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import CustomJSONParser.ServerReply;
import LocalPrefrences.SessionData;
import LocalPrefrences.UserDetails;
import dmax.dialog.SpotsDialog;
import Utils.Constants;

public class ChangePassword extends AppCompatActivity {
    MaterialEditText oldPass, newPass, cnfPass;
    Button change;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setTitle("Change Password");

        context = this;
        oldPass = (MaterialEditText) findViewById(R.id.oldPassword);
        newPass = (MaterialEditText) findViewById(R.id.newPassword);
        cnfPass = (MaterialEditText) findViewById(R.id.cnfnewPassword);
        change = (Button) findViewById(R.id.changePassword);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Change password request
                new RequestToChangePassword().execute();
            }
        });

        oldPass.setHelperText("Enter your Current Password");
        newPass.setHelperText("Password must contain atlest 8 character with an upper case, lower case and a digit");
        cnfPass.setHelperText("Enter new Password again");

        oldPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()==0)
                {
                    newPass.setEnabled(false);
                    cnfPass.setEnabled(false);
                    change.setEnabled(false);
                    change.setBackgroundResource(R.drawable.inactive_button_background);
                }
                else
                {
                    newPass.setEnabled(true);
                    cnfPass.setEnabled(true);
                    if(newPass.getText().toString().equals(cnfPass.getText().toString()) && newPass.getText().toString().length()>0)
                    {
                        change.setEnabled(true);
                        change.setBackgroundResource(R.drawable.active_button_background);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        newPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(newPass.getText().toString().length() == 0 || cnfPass.getText().toString().length() == 0)
                {
                    change.setEnabled(false);
                    change.setBackgroundResource(R.drawable.inactive_button_background);
                }

                validateNewPassword(s.toString());

                if(cnfPass.getText().toString().equals(newPass.getText().toString()))
                {
                    change.setEnabled(true);
                    change.setBackgroundResource(R.drawable.active_button_background);
                    cnfPass.setHelperText("Confirmed!");
                }
                else
                {
                    change.setEnabled(false);
                    change.setBackgroundResource(R.drawable.inactive_button_background);
                    cnfPass.setHelperText("Same password as above");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cnfPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(newPass.getText().toString().length() == 0 || cnfPass.getText().toString().length() == 0)
                {
                    change.setEnabled(false);
                    change.setBackgroundResource(R.drawable.inactive_button_background);
                }

                if(cnfPass.getText().toString().equals(newPass.getText().toString()))
                {
                    change.setEnabled(true);
                    change.setBackgroundResource(R.drawable.active_button_background);
                    cnfPass.setHelperText("Confirmed!");
                }
                else
                {
                    change.setEnabled(false);
                    change.setBackgroundResource(R.drawable.inactive_button_background);
                    cnfPass.setHelperText("Same password as above");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validateNewPassword(String s) {
        boolean isValid = false;

        if(s.equals(""))
        {
            isValid = false;
            newPass.setHelperText("Password must contain atlest 8 character with an upper case, lower case and a digit.");
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
                newPass.setHelperText("Password looks good!");
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
        newPass.setHelperText(errorMessage);
        return isValid;
    }

    private class RequestToChangePassword extends AsyncTask<String,String,String>{

        SpotsDialog spotsDialog = new SpotsDialog(context,"Changing password...");
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spotsDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.CHANGE_PASSWORD,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            spotsDialog.dismiss();
                            //If we are getting success from server
                            Log.i("Checking", response + " ");
                            if(new ServerReply(response).getStatus())
                            {
                                new SessionData(context).removeSessionKey();
                                new UserDetails(context).removeUserDetails();
                                Toast.makeText(context,"Password changed. Login with new password", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(context,SignIn.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                            spotsDialog.dismiss();
                            NetworkResponse networkResponse = error.networkResponse;
                            Log.e("Checking",networkResponse.statusCode +" : "+ new String(error.networkResponse.data));
                            Toast.makeText(context,new ServerReply(new String(error.networkResponse.data)).getReason()+" ",Toast.LENGTH_LONG).show();
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
                    params.put("authorization", "Bearer "+ new SessionData(context).getSessionKey());
                    return params;
                }
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request
                    params.put("oldPassword",oldPass.getText().toString());
                    params.put("newPassword",newPass.getText().toString());
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
