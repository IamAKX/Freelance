package com.akash.applications.freelance;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.EventLogTags;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akash.applications.freelance.ProfileDetails;
import com.akash.applications.freelance.ProjectPushing.ChatRoom;
import com.akash.applications.freelance.R;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Connector.ConnectionDetector;
import CustomJSONParser.JobDetailParser;
import CustomJSONParser.ServerReply;
import LocalPrefrences.SessionData;
import LocalPrefrences.UserDetails;
import Utils.Constants;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ViewProject extends AppCompatActivity
{
    TextView pName, category, desc, duration, deadline,name, accept, denied, postedOn, chat, call;
    Context context;
    ImageView empImg;
    String id,chatroomID,phoneNo,acceptedStatus;
    private String empID, empImage, empName;
    ConnectionDetector detector;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_project);
        getSupportActionBar().setTitle("JOB Details");



        if(getIntent().getExtras() != null)
        {
            id = getIntent().getExtras().getString("id");
            if(id == null)
                id = "nodi";
        }

        context = this;
        detector = new ConnectionDetector(context);
        empImg = (ImageView) findViewById(R.id.empImg);
        pName = (TextView) findViewById(R.id.ProjectName);
        category = (TextView) findViewById(R.id.ProjectCategory);
        desc = (TextView) findViewById(R.id.desc);
        duration = (TextView) findViewById(R.id.duration);
        deadline = (TextView) findViewById(R.id.deadline);
        name = (TextView) findViewById(R.id.empName);
        accept = (TextView) findViewById(R.id.correct);
        denied = (TextView) findViewById(R.id.wrong);
        chat = (TextView) findViewById(R.id.msgEmp);
        call = (TextView) findViewById(R.id.callEmp);
        empID = "";
        postedOn = (TextView) findViewById(R.id.postedOn);

        chat.setVisibility(View.GONE);
        call.setVisibility(View.GONE);

        accept.setEnabled(false);
        denied.setEnabled(false);
        findViewById(R.id.viewDetails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!empID.equals(""))
                    startActivity(new Intent(context, ProfileDetails.class).putExtra("id",empID));
            }
        });

        if(detector.isConnectingToInternet())
            new FetchProjectWork().execute();
        else
            detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");


        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(detector.isConnectingToInternet())
                    new AcceptProject().execute();
                else
                    detector.showSnackBar(v,"You lack Internet Connectivity");

            }
        });
        denied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(detector.isConnectingToInternet())
                    new DeniedProject().execute();
                else
                    detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");

            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+phoneNo));
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(ViewProject.this, new String[] { android.Manifest.permission.CALL_PHONE},Constants.PHONE_GROUP_CODE);
                else
                    startActivity(intent);
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chatroomID.equalsIgnoreCase(null))
                {
                    Toast.makeText(context,"Chatroom ID is not created, Accept and re-visit",Toast.LENGTH_LONG).show();
                    return;
                }
                startActivity(new Intent(context, ChatRoom.class)
                        .putExtra("image",Constants.USER_CURRENT_PROFILE_IMAGE+empImage)
                        .putExtra("name",empName)
                        .putExtra("chatroomId",chatroomID));
            }
        });
    }

    private class FetchProjectWork extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(final String... params) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.VIEW_JOB_DETAILS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " ");

                            JobDetailParser parser = new JobDetailParser(response);
                            pName.setText(parser.getName());
                            category.setText(parser.getCategory());
                            desc.setText(parser.getDesc());
                            duration.setText(Html.fromHtml(parser.getDuration()));
                            deadline.setText(Html.fromHtml(parser.getDeadline()));
                            Log.e("checking",parser.getDeadline()+"\n"+parser.getDuration());
                            Glide.with(context)
                                    .load(parser.getEimg())
                                    .bitmapTransform(new CropCircleTransformation(context))
                                    .placeholder(R.drawable.userdp)
                                    .error(R.drawable.userdp)
                                    .into(empImg);
                            name.setText(parser.getEname());
                            empID = parser.getEid();
                            empImage = parser.getEimg();
                            empName = parser.getName();
                            chatroomID = parser.getChatroomID();
                            phoneNo = parser.getPhoneNo();
                            acceptedStatus = parser.getAcceptedStatus();

                            if(acceptedStatus.equalsIgnoreCase("accept"))
                            {
                                //if accepted
                                chat.setVisibility(View.VISIBLE);
                                call.setVisibility(View.VISIBLE);
                                accept.setVisibility(View.GONE);
                                denied.setVisibility(View.GONE);
                                getSupportActionBar().setSubtitle("Project Accepted");
                            }
                            else
                            {
                                chat.setVisibility(View.GONE);
                                call.setVisibility(View.GONE);
                                accept.setVisibility(View.VISIBLE);
                                denied.setVisibility(View.VISIBLE);
                            }
                            accept.setEnabled(true);
                            denied.setEnabled(true);

                            findViewById(R.id.mainLayout).setVisibility(View.VISIBLE);
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
                    params.put("projectId",id);
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

    private class AcceptProject extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.ACCEPT_PROJECT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " ");
                            if(new ServerReply(response).getStatus())
                            {
                                try {
                                    JSONObject object = new JSONObject(response);
                                    chatroomID = object.getString("chatroomId");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                changeStatusInRealDB("A");
                                getSupportActionBar().setSubtitle("Project Accepted");
                                call.setVisibility(View.VISIBLE);
                                chat.setVisibility(View.VISIBLE);
                                accept.setVisibility(View.GONE);
                                denied.setVisibility(View.GONE);
                                Toast.makeText(context,"Project Accepted",Toast.LENGTH_LONG).show();
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
                    params.put("projectId",id);
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

    private class DeniedProject extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.REJECT_PROJECT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " ");
                            if(new ServerReply(response).getStatus())
                            {
                                changeStatusInRealDB("R");
                                Toast.makeText(context,"Project Rejected",Toast.LENGTH_LONG).show();
                                getSupportActionBar().setSubtitle("Project Rejected");
                                chat.setVisibility(View.GONE);
                                call.setVisibility(View.GONE);
                                finish();
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
                    params.put("projectId",id);
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

    private void changeStatusInRealDB(String r) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("ProjectAcceptance").child(id);
        Map<String, Object> map = new HashMap<>();
        map.put(new UserDetails(context).getUserName(),r);
        databaseReference.updateChildren(map);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case Constants.PHONE_GROUP_CODE:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED ))
                {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:"+phoneNo));
                    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions(ViewProject.this, new String[] { android.Manifest.permission.CALL_PHONE},Constants.PHONE_GROUP_CODE);
                    else
                        startActivity(intent);
                }
                else {
                    Toast.makeText(context,"Access to Phone call was denied",Toast.LENGTH_LONG);
                }
                break;

        }
    }

}
