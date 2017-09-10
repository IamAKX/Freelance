package com.akash.applications.freelance;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.akash.applications.freelance.MenuFragments.Project;
import com.akash.applications.freelance.ProfileSettingsOptions.SelectCategory;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eyalbira.loadingdots.LoadingDots;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import Connector.ConnectionDetector;
import CustomJSONParser.ServerReply;
import DataModel.CategoryModel;
import LocalPrefrences.OTPSessionHandler;
import LocalPrefrences.PreferenceKey;
import LocalPrefrences.SessionData;
import Utils.Constants;
import info.hoang8f.widget.FButton;
import Utils.CheckServiceStatus;
public class MainActivity extends Activity {

    LoadingDots dotProgress;
    private Context context;
    FButton registerBtn, loginBtn;
    ConnectionDetector detector;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        context = this;
        detector = new ConnectionDetector(context);
        startService(new Intent(context,FBMessagingService.class));
        Log.e("checking", "FB : "+new CheckServiceStatus(context).isRunning(FBMessagingService.class)+" ");
        registerBtn = (FButton) findViewById(R.id.btnRegister);
        loginBtn = (FButton) findViewById(R.id.btnSignIn);
        dotProgress = (LoadingDots) findViewById(R.id.loadingDotProgress);

        Log.e("checking", "FB : "+new CheckServiceStatus(context).isRunning(FBMessagingService.class)+" \n"+new CheckServiceStatus(context).isRunning(FBInstanceIDService.class));
        if(new SessionData(context).getSessionKey().equals(PreferenceKey.NO_KEY_FOUND))
        {
            registerBtn.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.VISIBLE);
        }
        else
        {
            if(detector.isConnectingToInternet())
            {
                Log.e("checking",detector.isConnectingToInternet()+" ++++++++++++++++++++++++++++++++++++++++++++++++++++");
                new CheckLoginSession().execute();
            }
            else
                detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity!");
        }

        final ConnectionDetector cd = new ConnectionDetector(getBaseContext());
        //when clicked on Register button
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cd.isConnectingToInternet()) {
                    startActivity(new Intent(getBaseContext(),Register.class));
                }
                else
                    cd.showSnackBar(v,"It seems you are not connected to Internet");
            }
        });

        //when clicked on Sign In Button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cd.isConnectingToInternet()) {
                    startActivity(new Intent(getBaseContext(),SignIn.class));
                }
                else
                    cd.showSnackBar(v,"It seems you are not connected to Internet");

            }
        });


    }

    class CheckLoginSession extends AsyncTask<String,String,String>
    {

        @Override
        protected String doInBackground(String... params) {

            //Performing fake background task
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LOGIN_SESSION,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " ");

                            fetchCategory();
//                            dotProgress.setVisibility(View.GONE);
//                            dotProgress.stopAnimation();
//     startActivity(new Intent(context,Home.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                            NetworkResponse networkResponse = error.networkResponse;

                            dotProgress.stopAnimation();
                            dotProgress.setVisibility(View.GONE);
                            loginBtn.setVisibility(View.VISIBLE);
                            registerBtn.setVisibility(View.VISIBLE);
                            if(networkResponse != null && networkResponse.data != null)
                            {
                                switch (networkResponse.statusCode)
                                {
                                    case 500:
                                    default:
                                        dotProgress.stopAnimation();
                                        dotProgress.setVisibility(View.GONE);
                                        loginBtn.setVisibility(View.VISIBLE);
                                        registerBtn.setVisibility(View.VISIBLE);
                                        break;
                                }
                            }
                        }

                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request
                    params.put("authorization", "Bearer "+ new SessionData(context).getSessionKey());
                    Log.i("Checking token",new SessionData(context).getSessionKey());
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
            loginBtn.setVisibility(View.GONE);
            registerBtn.setVisibility(View.GONE);
            dotProgress.setVisibility(View.VISIBLE);
        }

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
                        dotProgress.setVisibility(View.GONE);
                        dotProgress.stopAnimation();
                        startActivity(new Intent(context,Home.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
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


}