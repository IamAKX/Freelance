package com.akash.applications.freelance;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import CustomJSONParser.ServerReply;
import LocalPrefrences.SessionData;
import Utils.Constants;

public class FBInstanceIDService extends FirebaseInstanceIdService {

    Context context;
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        context = getBaseContext();

//        try {
//            FirebaseInstanceId.getInstance().deleteInstanceId();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        String newToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("checking",newToken+" ");
        updateTokenToServer(newToken);
    }


    private void updateTokenToServer(final String newToken) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.FCM_TOKEN_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        Log.i("Checking", response + " ");

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want

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
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put("authorization", "Bearer "+ new SessionData(context).getSessionKey());
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put("token",newToken);
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
