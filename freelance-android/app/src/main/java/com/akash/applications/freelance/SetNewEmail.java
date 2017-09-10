package com.akash.applications.freelance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Connector.ConnectionDetector;
import CustomJSONParser.ServerReply;
import LocalPrefrences.SessionData;
import LocalPrefrences.UserDetails;
import Utils.Constants;
import dmax.dialog.SpotsDialog;

public class SetNewEmail extends AppCompatActivity {

    MaterialEditText oldEmail, newEmail;
    Button change;
    Context context;
    ConnectionDetector detector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_email);
        getSupportActionBar().setTitle("Change Email");
        context = this;
        detector = new ConnectionDetector(context);
        oldEmail = (MaterialEditText) findViewById(R.id.old_Email);
        newEmail = (MaterialEditText) findViewById(R.id.new_Email);
        change = (Button) findViewById(R.id.change_email);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!detector.isConnectingToInternet())
                {
                    detector.showSnackBar(v,"You lack Internet Connectivity");
                    return;
                }

//                startActivity(new Intent(context,SignIn.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
//                new SessionData(context).removeSessionKey();
                new VerifyEmail(oldEmail.getText().toString()).execute();
            }
        });

        oldEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(checkOldEmail() && checkNewEmail())
                {
                    change.setEnabled(true);
                    change.setBackgroundResource(R.drawable.active_button_background);
                }
                else
                {
                    change.setEnabled(false);
                    change.setBackgroundResource(R.drawable.inactive_button_background);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        newEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(checkOldEmail() && checkNewEmail())
                {
                    change.setEnabled(true);
                    change.setBackgroundResource(R.drawable.active_button_background);
                }
                else
                {
                    change.setEnabled(false);
                    change.setBackgroundResource(R.drawable.inactive_button_background);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean checkNewEmail() {
        boolean isValid = false;
        newEmail.setHelperText("Enter new email id");
        if(newEmail.getText().toString().length()==0)
            return isValid;

        if(newEmail.getText().toString().equals(oldEmail.getText().toString()))
        {
            newEmail.setHelperText("New email cannot be same as old email");
            isValid = false;
            return isValid;
        }

        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(Constants.EMAIL_PATTERN);
        matcher = pattern.matcher(newEmail.getText().toString());
        isValid  = matcher.matches();
        if(!isValid)
            newEmail.setHelperText("Enter a valid email id");
        return isValid;
    }

    private boolean checkOldEmail() {
        boolean isValid = false;
        oldEmail.setHelperText("Enter old email id");
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(Constants.EMAIL_PATTERN);
        matcher = pattern.matcher(oldEmail.getText().toString());
        isValid  = matcher.matches();
        return isValid;
    }

    private class VerifyEmail extends AsyncTask<String,String,String> {
        String email;
        SpotsDialog spotsDialog;

        public VerifyEmail(String email) {
            this.email = email;
            this.spotsDialog = new SpotsDialog(context, "Verifing your email");
            spotsDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.VERIFY_EMAIL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " test");

                                spotsDialog.dismiss();
                                oldEmail.setError("No user with this email is registered");

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want

                            NetworkResponse networkResponse = error.networkResponse;
                            ////Log.i("Checking", new String(error.networkResponse.data));
                            if(new ServerReply(new String(error.networkResponse.data)).getReason().equalsIgnoreCase("user exists"))
                            {
                                spotsDialog.setMessage("Changing email..");
                                changeEmail();

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


        public String changeEmail(){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.CHANGE_EMAIL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server

                            spotsDialog.dismiss();
                            Log.i("Checking", response + " ");
                            if(new ServerReply(response).getStatus())
                            {
                                new UserDetails(context).removeUserDetails();

                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Email changed successfully");
                                builder.setMessage("Verification link is sent to your new email. You will be logged out now. Verify the email now?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new UserDetails(context).removeUserDetails();
                                        dialog.dismiss();
                                        //Launch email client app
                                        Intent intent1 = new Intent(context, SignIn.class);
                                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent1);

                                        Intent intent = new Intent(Intent.ACTION_MAIN);
                                        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                                        startActivity(intent);


                                    }
                                });
                                builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        new UserDetails(context).removeUserDetails();
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

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                            spotsDialog.dismiss();
                            NetworkResponse networkResponse = error.networkResponse;
                            ////Log.i("Checking", new String(error.networkResponse.data));
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
                    params.put("oldUsername",oldEmail.getText().toString());
                    params.put("newUsername",newEmail.getText().toString());
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
