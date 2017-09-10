package com.akash.applications.freelance;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.akash.applications.freelance.ProfileSettingsOptions.SelectAppMode;
import com.akash.applications.freelance.ProfileSettingsOptions.SelectCategory;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import CustomJSONParser.ServerReply;
import LocalPrefrences.SessionData;
import LocalPrefrences.UserDetails;
import Utils.Constants;
import dmax.dialog.SpotsDialog;

import static com.akash.applications.freelance.Home.navMenu;

/**
 * Created by bishal on 3/5/2017.
 */

public class UserModeManager extends AsyncTask<String,String,String> {
    Context context;
    SpotsDialog  dialog;
    String mode;
    Activity activity = null;

    public UserModeManager(Context context, String mode) {
        this.context = context;
        this.mode = mode;
        dialog = new SpotsDialog(context,"Switching mode. Please wait.");
    }

    public UserModeManager(Context context, String mode, Activity activity) {
        this.context = context;
        this.mode = mode;
        this.activity = activity;
        dialog = new SpotsDialog(context,"Switching mode. Please wait.");
    }

    @Override
    protected void onPreExecute() {
         super.onPreExecute();
        dialog.show();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if(mode.equalsIgnoreCase("work"))
        {
            navMenu.findItem(R.id.PostProject).setVisible(false);
            navMenu.findItem(R.id.Notification).setVisible(true);
            showChooseCategory();
        }
        else
        {
            navMenu.findItem(R.id.PostProject).setVisible(true);
            navMenu.findItem(R.id.Notification).setVisible(false);
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SAVE_MODE,
        new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //If we are getting success from server
                dialog.dismiss();
                Log.i("checking",response);
                if(new ServerReply(response).getStatus())
                {
                    Toast.makeText(context,"Mode saved!",Toast.LENGTH_SHORT).show();
                    new UserDetails(context).setUserMode(mode);
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //You can handle error here if you want
                Log.e("Checking","error");
                dialog.dismiss();
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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put("mode",mode.toLowerCase());
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

    private void showChooseCategory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("Switching to WORK mode requires the selection of employee category you are willing to work as. Do you wish to select the category again or continue with previous selection?");
        builder.setNegativeButton("Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                context.startActivity(new Intent(context,SelectCategory.class));
                if(activity != null)
                    activity.finish();
            }
        });
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

}
