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
import android.view.WindowManager;
import android.widget.Button;
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
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import CustomJSONParser.ServerReply;
import LocalPrefrences.SessionData;
import LocalPrefrences.UserDetails;
import dmax.dialog.SpotsDialog;
import Utils.Constants;
public class DeleteAccount extends AppCompatActivity {
    TextView changeEmail, changePhone;
    MaterialEditText phoneNumber;
    Button deleteBtn;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getSupportActionBar().setTitle("Delete Account");
        context = this;
        changeEmail = (TextView) findViewById(R.id.delete_change_email);
        changePhone = (TextView) findViewById(R.id.delete_change_number);
        phoneNumber = (MaterialEditText) findViewById(R.id.cnfPhone);
        deleteBtn = (Button) findViewById(R.id.deleteBtn);

        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,SetNewEmail.class));
                finish();
            }
        });

        changePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,SetNewPhone.class));
                finish();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phoneNumber.getText().toString().length()==10 || phoneNumber.getText().toString().length()==13)
                    showDeleteAlert();
                else
                    Toast.makeText(context,"Phone number is not valid",Toast.LENGTH_LONG).show();
//                new SessionData(context).removeSessionKey();
//                //send delete account request
//                startActivity(new Intent(context,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
//                finish();

            }
        });

        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()==10)
                {
                    deleteBtn.setEnabled(true);
                    deleteBtn.setBackgroundResource(R.color.adctive_red);
                }
                else
                {
                    deleteBtn.setEnabled(false);
                    deleteBtn.setBackgroundResource(R.color.inactive_red);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    
    private void showDeleteAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("You are going to delete your account. This will delete your information and your project permanently. Do you want to proceed?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               new RequestTodeleteAccount().execute();
                dialog.dismiss();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class RequestTodeleteAccount extends AsyncTask<String,String,String>{

        SpotsDialog spotsDialog = new SpotsDialog(context,"Deleting user");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spotsDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.DELETE_ACCOUNT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " ");
                            if(new ServerReply(response).getStatus())
                            {
                                new SessionData(context).removeSessionKey();
                                new UserDetails(context).removeUserDetails();
                                startActivity(new Intent(context,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
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
                            Toast.makeText(context,new String(networkResponse.data)+" ",Toast.LENGTH_LONG).show();
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
                    params.put("authorization", "Bearer "+ new SessionData(context).getSessionKey());
                    return params;
                }
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request
                    params.put("phone",phoneNumber.getText().toString());
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
