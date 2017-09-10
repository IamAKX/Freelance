package com.akash.applications.freelance.ProjectPushing;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.akash.applications.freelance.Home;
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
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import Adapters.UserSelectionAdapter;
import Connector.ConnectionDetector;
import CustomJSONParser.ServerReply;
import CustomJSONParser.UserSelectionParser;
import CustomJSONParser.WorkerListParser;
import DataModel.UserSelectionModel;
import LocalPrefrences.ProjectTimmer;
import LocalPrefrences.SessionData;

import Utils.CheckServiceStatus;
import Utils.Constants;
import dmax.dialog.SpotsDialog;
import lb.library.PinnedHeaderListView;

import static java.lang.Thread.sleep;

public class UserSelection extends AppCompatActivity {

    public static HashMap<String,String> selectUsersList = new HashMap<>();
    Context context;
    LayoutInflater inflater;
    PinnedHeaderListView listView;
    ArrayList<UserSelectionModel> arrayList = new ArrayList<>();
    UserSelectionAdapter selectionAdapter;
    TextView postProjectButton;
    CheckBox checkBoxSelectAll;
    String name,desc,category,start,end,unit,deadline,timeout;
    DatabaseReference databaseReference, rootNameProject;
    LoadingDots loadingDots;
    private String projectID ="";
    ConnectionDetector detector;
    EditText searchField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = LayoutInflater.from(UserSelection.this);
        setContentView(R.layout.activity_user_selection);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        context = this;
        getFromIntent();
        detector = new ConnectionDetector(context);
        getSupportActionBar().setTitle("Select Freelancers");
        listView = (PinnedHeaderListView) findViewById(R.id.listWorkers);
        postProjectButton = (TextView) findViewById(R.id.userListPostButton);
        checkBoxSelectAll = (CheckBox) findViewById(R.id.listUserCheckAll);
        loadingDots = (LoadingDots) findViewById(R.id.user_selection_loading_dots);
        loadingDots.setVisibility(View.VISIBLE);
        searchField = (EditText) findViewById(R.id.search);
        if(detector.isConnectingToInternet())
            new FetchWorkers().execute();
        else
            detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");

        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot();
        rootNameProject = databaseReference.getRoot().child("ProjectAcceptance");
        checkBoxSelectAll.setVisibility(View.GONE);
        checkBoxSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked)
                {
                    selectUsersList.clear();
                    for(UserSelectionModel model:arrayList)
                        model.setSelectedState(isChecked);
                }
                else
                {
                    for(UserSelectionModel model:arrayList)
                    {
                        model.setSelectedState(isChecked);
                        selectUsersList.put(model.getId(),model.getName());
                    }
                }

                selectionAdapter.notifyDataSetChanged();
                listView.invalidate();
            }
        });

        postProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("checking", selectUsersList.values().toString());
                if(detector.isConnectingToInternet())
                {
                    if(selectUsersList.size() > 0)
                        new BroadCastProject().execute();
                    else
                        Toast.makeText(context,"No freelancer is selected",Toast.LENGTH_LONG).show();
                }
                else
                    detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");

            }
        });

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchName = searchField.getText().toString();
                if(searchName.equalsIgnoreCase(""))
                {
                    setAdapterWithArrayList(arrayList);
                }
                else
                {
                    ArrayList<UserSelectionModel> newArrayList = new ArrayList<UserSelectionModel>();
                    for(UserSelectionModel model : arrayList)
                    {
                        if(model.getName().toLowerCase().contains(searchName.toLowerCase()))
                            newArrayList.add(model);
                    }
                    setAdapterWithArrayList(newArrayList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getFromIntent()
    {
        name = getIntent().getStringExtra("pName");
        desc = getIntent().getStringExtra("pDesc");
        category = getIntent().getStringExtra("pCategory");
        start = getIntent().getStringExtra("pStart");
        end = getIntent().getStringExtra("pEnd");
        unit = getIntent().getStringExtra("pUnit");
        deadline = getIntent().getStringExtra("pDeadline");
        timeout = getIntent().getStringExtra("timeout");
    }

    private void showProjectAcceptanceTimmer(final String key) {

//        if(new CheckServiceStatus(getBaseContext()).isRunning(ProjectTimmerService.class))
//            stopService(new Intent(context,ProjectTimmerService.class));

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = inflater.inflate(R.layout.project_timmer,null);
        final TextView min = (TextView) dialogView.findViewById(R.id.min);
        final TextView sec = (TextView) dialogView.findViewById(R.id.sec);
        final LinearLayout header = (LinearLayout) dialogView.findViewById(R.id.dialogHeader);
        final TextView listOfEmployee = (TextView) dialogView.findViewById(R.id.acceptanceResult);
        builder.setView(dialogView);
        final HashMap<String,String> listMap = new HashMap<>();
        rootNameProject.child(projectID).addChildEventListener(new ChildEventListener() {
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

                header.setVisibility(View.GONE);
            }
        };
        final Thread t = new Thread(runable);
        t.start();
        builder.setPositiveButton("Minimize", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context,"Check your project status in Project Section",Toast.LENGTH_LONG).show();
                startActivity(new Intent(context, Home.class).putExtra("open","project"));
                dialog.dismiss();

            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }


    private class FetchWorkers extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }



        @Override
        protected String doInBackground(String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.FETCH_WORKER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " ");
                                fillEmployeeArrayList(response);
                            checkBoxSelectAll.setVisibility(View.VISIBLE);
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
                                    case 500:
                                        break;
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("checking","end");
        }
    }

    private void fillEmployeeArrayList(String response) {

        try {

            JSONArray collection = new JSONArray(response);
            for (int i = 0; i < collection.length(); i++) {
                arrayList.add(new UserSelectionParser().getModel(collection.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setAdapterWithArrayList(arrayList);
    }

    private void setAdapterWithArrayList(ArrayList<UserSelectionModel> arrayList) {

        Collections.sort(arrayList, new Comparator<UserSelectionModel>() {
            @Override
            public int compare(UserSelectionModel o1, UserSelectionModel o2) {
                char lhs = TextUtils.isEmpty(o1.getName()) ? ' ' : o1.getName().charAt(0);
                char rhs = TextUtils.isEmpty(o2.getName()) ? ' ' : o2.getName().charAt(0);
                int firstLetterComparision = Character.toUpperCase(lhs) - Character.toUpperCase(rhs);
                if(firstLetterComparision == 0)
                    return o1.getName().compareTo(o2.getName());
                return firstLetterComparision;
            }
        });

        selectionAdapter = new UserSelectionAdapter(context,arrayList,inflater);
        selectionAdapter.setPinnedHeaderBackgroundColor(getResources().getColor(android.R.color.transparent));
        selectionAdapter.setPinnedHeaderTextColor(getResources().getColor(R.color.colorAccent));
        listView.setPinnedHeaderView(inflater.inflate(R.layout.pinheaded_side_header,listView,false));
        listView.setAdapter(selectionAdapter);

        listView.setOnScrollListener(selectionAdapter);
        listView.setEnableHeaderTransparencyChanges(false);
        selectionAdapter.notifyDataSetChanged();
        listView.invalidate();
        loadingDots.setVisibility(View.GONE);
    }


    private class BroadCastProject extends AsyncTask<String,String,String>{

        SpotsDialog spotsDialog = new SpotsDialog(context,"Posting project to "+selectUsersList.size()+" Freelancers");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spotsDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {


            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.BROADCAST_PROJECT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " ");

                            if(new ServerReply(response).getStatus()) {
                                try {
                                    JSONObject object = new JSONObject(response);
                                    if (object.has("id"))
                                        projectID = object.getString("id");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                List<String> idlists = new ArrayList<>(selectUsersList.values());
                                for(String id : idlists)
                                {
                                    Map<String, Object> map = new HashMap<>();
                                    DatabaseReference newProj = rootNameProject.child(projectID);
                                    map.put(id,"NR");
                                    newProj.updateChildren(map);
                                }
                                spotsDialog.dismiss();
                                new ProjectTimmer(getBaseContext()).setTimmer(projectID, timeout);
                                showProjectAcceptanceTimmer(projectID);

                            }

                            if(spotsDialog.isShowing())
                                spotsDialog.dismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                            spotsDialog.dismiss();
                            String s = new String(error.networkResponse.data);

                            Toast.makeText(context,new ServerReply(s).getReason(),Toast.LENGTH_LONG).show();
                            NetworkResponse networkResponse = error.networkResponse;

                            if(networkResponse != null && networkResponse.data != null)
                            {
                                switch (networkResponse.statusCode)
                                {
                                    case 500:
                                        break;
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
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request
                    params.put("name",name);
                    params.put("description",desc);
                    params.put("startDate",start);
                    params.put("endDate",end);
                    params.put("type",unit.toLowerCase());
                    params.put("category",category);
                    List<String> idlists = new ArrayList<>(selectUsersList.keySet());
                    String ids = TextUtils.join(",",idlists);
                    params.put("participant",ids);
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
