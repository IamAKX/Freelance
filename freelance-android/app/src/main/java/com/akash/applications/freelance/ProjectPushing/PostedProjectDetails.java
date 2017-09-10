package com.akash.applications.freelance.ProjectPushing;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import com.eyalbira.loadingdots.LoadingDots;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import Adapters.AcceptedUserAdapter;
import Adapters.EmployeeAdapter;
import Connector.ConnectionDetector;
import CustomJSONParser.PostedProjectDetailsParser;
import CustomJSONParser.ServerReply;
import DataModel.AcceptedUserModel;
import LocalPrefrences.ProjectTimmer;
import LocalPrefrences.SessionData;
import Utils.Constants;
import Utils.DateFormatManager;
import dmax.dialog.SpotsDialog;

import static java.lang.Thread.sleep;

public class PostedProjectDetails extends AppCompatActivity {

    String id;
    Context context;
    TextView name, desc, duration, deadline, timmerTextView, postedOn, status, nobodyready;
    LoadingDots loadingDots;
    LinearLayout layout1,layout2;
    ConnectionDetector detector;
    ArrayList<AcceptedUserModel> list = new ArrayList<>();
    AcceptedUserAdapter adapter;
    RecyclerView recyclerView;
    Switch closeProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posted_project_details);

        context = this;
        detector = new ConnectionDetector(context);
        getSupportActionBar().setTitle("Project Details");
        getSupportActionBar().setSubtitle("In Progress");

        id = getIntent().getStringExtra("id");
        name = (TextView) findViewById(R.id.pName);
        desc = (TextView) findViewById(R.id.pDescription);
        duration = (TextView) findViewById(R.id.pDuration);
        deadline = (TextView) findViewById(R.id.pDeadline);
        postedOn = (TextView) findViewById(R.id.pPostedOn);
        status = (TextView) findViewById(R.id.pStatus);
        loadingDots = (LoadingDots) findViewById(R.id.postedProjectLoadingDots);
        layout1 = (LinearLayout) findViewById(R.id.postedProjectLL1);
        layout2 = (LinearLayout) findViewById(R.id.ll2);
        timmerTextView = (TextView) findViewById(R.id.timmerTextview);
        nobodyready = (TextView) findViewById(R.id.nobodyready);
        closeProject = (Switch) findViewById(R.id.closeProjectSwitch);

        layout2.setVisibility(View.GONE);
        recyclerView = (RecyclerView) findViewById(R.id.postedProjectRecyclerView);
        loadingDots.setVisibility(View.VISIBLE);
        layout1.setVisibility(View.GONE);
        if (detector.isConnectingToInternet())
            new FetchProjectDetails().execute();
        else
            detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");



        final Handler handler = new Handler();
        Runnable runable = new Runnable() {
            int secLeft = Integer.parseInt(new ProjectTimmer(getBaseContext()).getTimmer(id));

            @Override
            public void run() {
                while (secLeft > 0){
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String min = String.valueOf((secLeft/1000/60));
                            String sec = String.valueOf((secLeft/1000)%60);
                            timmerTextView.setText(Html.fromHtml("Available Freelancers will be listed in <br/><b>"+min+" min "+sec+" sec </b>"));
                            secLeft -= 1000;
                            new ProjectTimmer(getBaseContext()).setTimmer(id,String.valueOf(secLeft));
                        }
                    });

                }
                fillRecyclerList();
                changeProjectStatus();
            }
        };
        final Thread t = new Thread(runable);
        t.start();

        closeProject.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked)
                {
                    showCloseAlert();
                }
            }
        });
    }

    private void showCloseAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("You are about to close this Project, which means your job is accomplished. Do you want to proceed?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(detector.isConnectingToInternet())
                    new CloseProject().execute();
                else
                    detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connection");

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

    private void changeProjectStatus() {
        if(!detector.isConnectingToInternet())
        {
            detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");
            return;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.CHANGE_PROJECT_STATUS_AFTER_WAITING_TIME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        Log.i("Checking", response + " ");
                        if(new ServerReply(response).getStatus())
                        {
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want

                        NetworkResponse networkResponse = error.networkResponse;
                        Log.i("Checking", new String(error.networkResponse.data));
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

    }

    private class FetchProjectDetails extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(final String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.VIEW_POSTED_PROJECT_DETAILS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " ");

                            PostedProjectDetailsParser parser = new PostedProjectDetailsParser(response);
                            name.setText(parser.getName().toUpperCase());
                            desc.setText(parser.getDesc());
                            duration.setText(parser.getDuration());


                            deadline.setText(DateFormatManager.utcTODate(parser.getDeadline(),"dd MMM, yyyy"));
                            postedOn.setText(DateFormatManager.utcTODate(parser.getDeadline(),"dd MMM, yyyy hh:mm a"));
                            status.setText(parser.getStatus());
                            getSupportActionBar().setSubtitle(parser.getStatus());
                            loadingDots.setVisibility(View.GONE);
                            layout1.setVisibility(View.VISIBLE);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want

                            NetworkResponse networkResponse = error.networkResponse;
                            Log.i("Checking", new String(error.networkResponse.data));
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

    public void fillRecyclerList()
    {

        if(!detector.isConnectingToInternet())
        {
            detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");
            return;
        }
//        Toast.makeText(context,"Fetching Freelancers accepted this project",Toast.LENGTH_LONG).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.GET_USERS_WHO_ACCEPTED_PROJECT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        layout2.setVisibility(View.VISIBLE);
                        Log.i("Checking", response + " ");
                        try {
                            JSONArray array = new JSONArray(response);
                            for(int i = 0; i<array.length();i++)
                            {
                                JSONObject object = array.getJSONObject(i);
                                AcceptedUserModel model = new AcceptedUserModel(
                                        object.getString("_id"),
                                        object.getString("name"),
                                        object.getString("image"),
                                        object.getString("chatroomId"),
                                        object.getString("status")
                                );
                                list.add(model);
                                setAdapterInRecyclerView();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(list.size()>0)
                        {
                            setAdapterInRecyclerView();
                            Log.e("checking","List "+list.size());
                        }
                        else
                        {
                            nobodyready.setTextColor(getResources().getColor(R.color.adctive_red));
                            nobodyready.setText("NO FREELANCER IS READY FOR THIS PROJECT");
                            nobodyready.setGravity(Gravity.CENTER_HORIZONTAL);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want

                        NetworkResponse networkResponse = error.networkResponse;
                        Log.i("Checking", new String(error.networkResponse.data));
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
    }

    private void setAdapterInRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AcceptedUserAdapter(context,list,name.getText().toString(),id);
        recyclerView.setAdapter(adapter);
        recyclerView.invalidate();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.posted_project_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuid = item.getItemId();
        if(menuid == R.id.menu_item_info)
            showProjectAcceptanceTimmer(id);
            return true;
    }

    private void showProjectAcceptanceTimmer(final String key) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot();
        DatabaseReference rootNameProject = databaseReference.getRoot().child("ProjectAcceptance");
        LayoutInflater inflater = LayoutInflater.from(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = inflater.inflate(R.layout.project_timmer,null);
        final TextView min = (TextView) dialogView.findViewById(R.id.min);
        final TextView sec = (TextView) dialogView.findViewById(R.id.sec);
        final LinearLayout dialogHeader = (LinearLayout) dialogView.findViewById(R.id.dialogHeader);
        final TextView listOfEmployee = (TextView) dialogView.findViewById(R.id.acceptanceResult);
        builder.setView(dialogView);
        final HashMap<String,String> listMap = new HashMap<>();
        rootNameProject.child(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String name = dataSnapshot.getKey();
                String status = dataSnapshot.getValue().toString();

                if(status.equalsIgnoreCase("NR"))
                    listMap.put(name,"<font color=\"#f9c91d\"><b>"+name+"</b> - <i>Not responding</i></font><br/><br/>");
                else
                if(status.equalsIgnoreCase("A"))
                    listMap.put(name,"<font color=\"#15BC34\"><b>"+name+"</b> - <i>Accepted</i></font><br/><br/>");
                else
                    listMap.put(name,"<font color=\"#ff1a00\"><b>"+name+"</b> - <i>Rejected</i></font><br/><br/>");
                updateListOfEmployee();
            }

            private void updateListOfEmployee() {
                listOfEmployee.setText("");
                Collection<String> values = listMap.values();
                for(String s : values)
                    listOfEmployee.append(Html.fromHtml(s));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.i("checking",dataSnapshot.getValue()+" ");
                String name = dataSnapshot.getKey();
                String status = dataSnapshot.getValue().toString();
                if(status.equalsIgnoreCase("NR"))
                    listMap.put(name,"<font color=\"#f9c91d\"><b>"+name+"</b> - <i>Not responding</i></font><br/><br/>");
                else
                if(status.equalsIgnoreCase("A"))
                    listMap.put(name,"<font color=\"#15BC34\"><b>"+name+"</b> - <i>Accepted</i></font><br/><br/>");
                else
                    listMap.put(name,"<font color=\"#ff1a00\"><b>"+name+"</b> - <i>Rejected</i></font><br/><br/>");
                updateListOfEmployee();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        final Handler handler = new Handler();
        Runnable runable = new Runnable() {
            int secLeft = Integer.parseInt(new ProjectTimmer(getBaseContext()).getTimmer(key));
            @Override
            public void run() {
                while (secLeft > 0){
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            min.setText(String.valueOf((secLeft/1000/60)));
                            sec.setText(String.valueOf((secLeft/1000)%60));
                            secLeft -= 1000;
                            new ProjectTimmer(getBaseContext()).setTimmer(key,String.valueOf(secLeft));
                        }
                    });

                }
//                min.setText("0");
//                sec.setText("00");
                dialogHeader.setVisibility(View.GONE);
            }
        };
        final Thread t = new Thread(runable);
        t.start();
        builder.setPositiveButton("Minimize", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    private class CloseProject extends AsyncTask<String,String,String>{
        SpotsDialog spotsDialog = new SpotsDialog(context);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spotsDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.CLOSE_PROJECT_BY_EMPLOYER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " ");
                            if(new ServerReply(response).getStatus())
                            {
                                spotsDialog.dismiss();
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                            spotsDialog.dismiss();
                            NetworkResponse networkResponse = error.networkResponse;
                            Log.i("Checking", new String(error.networkResponse.data));
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
}
