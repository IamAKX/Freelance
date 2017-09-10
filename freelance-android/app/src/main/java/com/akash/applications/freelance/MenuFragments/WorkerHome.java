package com.akash.applications.freelance.MenuFragments;


import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akash.applications.freelance.ProfileDetails;
import com.akash.applications.freelance.ProfileSettingsOptions.SelectCategory;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Adapters.OnGoingProjectAdapter;
import Connector.ConnectionDetector;
import CustomJSONParser.ServerReply;
import DataModel.OnGoingProjectModel;
import LocalPrefrences.PreferenceKey;
import LocalPrefrences.SessionData;
import LocalPrefrences.UserDetails;
import Utils.CategoryIcon;
import Utils.Constants;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkerHome extends Fragment {


    ProgressBar progressBar;
    TextView textViewNotFound, rupees,paise;
    RecyclerView recyclerView;
    OnGoingProjectAdapter adapter;
    ArrayList<OnGoingProjectModel> list = new ArrayList<>();
    LinearLayout categoryLayout, employeeLayout;
    Context context;
    ConnectionDetector detector;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar3);
        textViewNotFound = (TextView) getView().findViewById(R.id.tvNotFound);
        recyclerView = (RecyclerView) getView().findViewById(R.id.worker_home_recyclerview);
        categoryLayout = (LinearLayout) getView().findViewById(R.id.llCAT);
        employeeLayout = (LinearLayout) getView().findViewById(R.id.llEMP);
        rupees = (TextView) getView().findViewById(R.id.rupees);
        paise = (TextView) getView().findViewById(R.id.paise);

        context = getContext();
        detector = new ConnectionDetector(context);
        categoryLayout.setLayoutTransition(new LayoutTransition());
        employeeLayout.setLayoutTransition(new LayoutTransition());

        ImageView addCategory = new ImageView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(80,80);
        layoutParams.setMargins(10,10,10,10);

        Glide.with(context)
                .load(android.R.drawable.ic_input_add)
                .placeholder(android.R.drawable.ic_input_add)
                .bitmapTransform(new CropCircleTransformation(context))
                .error(android.R.drawable.ic_input_add)
                .into(addCategory);
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SelectCategory.class));
            }
        });
        addCategory.setLayoutParams(layoutParams);
        categoryLayout.addView(addCategory);

        String setCategories = new UserDetails(context).getUserCategory();
        if(!setCategories.equalsIgnoreCase(PreferenceKey.NO_KEY_FOUND))
        {
            String[] catArray = setCategories.substring(1,setCategories.indexOf(']')).split(",");
            for(final String cat : catArray)
            {
                ImageView image = new ImageView(context);
                layoutParams = new LinearLayout.LayoutParams(80,80);
                layoutParams.setMargins(10,10,10,10);
                Glide.with(context)
                        .load(CategoryIcon.getIcon(cat))
                        .placeholder(R.drawable.info)
                       // .bitmapTransform(new CropCircleTransformation(context))
                        .error(R.drawable.info)
                        .into(image);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context,cat,Toast.LENGTH_SHORT).show();
                    }
                });
                image.setLayoutParams(layoutParams);
                categoryLayout.addView(image);
            }
        }

        if(detector.isConnectingToInternet())
        {
            new LoadMoneyEarned().execute();
            new LoadCurrentJobs().execute();
            setRecentlyWorked();
        }
        else
            detector.showSnackBar(getActivity().getWindow().getDecorView(),"You lack Internet connection");
    }

    class LoadCurrentJobs extends AsyncTask<String,String,String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.FETCH_ONGOING_PROJECTS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " ");
                            try {
                                JSONArray array = new JSONArray(response);
                                for(int i=0; i<array.length(); i++)
                                {
                                    JSONObject object = array.getJSONObject(i);
                                    OnGoingProjectModel model = new OnGoingProjectModel(
                                            object.getString("employerId"),
                                            object.getString("projectId"),
                                            object.getString("employerName"),
                                            object.getString("employerImage"),
                                            object.getString("phone"),
                                            object.getString("aDate"),
                                            object.getString("createdAt"),
                                            object.getString("chatroomId")
                                    );
                                    list.add(model);
                                    setRecyclerViewItems();
                                }
                            } catch (JSONException e) {
                                Log.e("checking",e.toString());
                            }

                            if(list.size()==0)
                            {
                                textViewNotFound.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                            else
                            {
                                textViewNotFound.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }


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
                    Map<String,String> params = new HashMap<>();
                    params.put("authorization", "Bearer "+ new SessionData(context).getSessionKey());
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

            return null;
        }
    }

    private void setRecyclerViewItems() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new OnGoingProjectAdapter(context,list,getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.invalidate();
        adapter.notifyDataSetChanged();
    }

    public void setRecentlyWorked()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.RECENTLY_WORKED,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        Log.i("Checking", response + " ");

                        try {
                            JSONArray array = new JSONArray(response);
                            for(int i=0; i<array.length(); i++)
                            {
                                final JSONObject object = array.getJSONObject(i);
                                ImageView image = new ImageView(context);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(80, 80);
                                layoutParams.setMargins(10,10,10,10);
                                Glide.with(context)
                                        .load(Constants.USER_CURRENT_PROFILE_IMAGE + object.getString("image"))
                                        .placeholder(R.drawable.userdp)
                                        .bitmapTransform(new CropCircleTransformation(context))
                                        .error(R.drawable.userdp)
                                        .into(image);
                                image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            Toast.makeText(context,object.getString("name"),Toast.LENGTH_SHORT).show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                image.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View v) {
                                        try {
                                            startActivity(new Intent(context, ProfileDetails.class)
                                                    .putExtra("id",object.getString("_id"))
                                                    .putExtra("image",Constants.USER_CURRENT_PROFILE_IMAGE+object.getString("image"))
                                                    .putExtra("name",object.getString("name")));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        return true;
                                    }
                                });
                                image.setLayoutParams(layoutParams);
                                employeeLayout.addView(image);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
                Map<String,String> params = new HashMap<>();
                params.put("authorization", "Bearer "+ new SessionData(context).getSessionKey());
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

    private class LoadMoneyEarned extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.GET_WALLET_AMOUNT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " money");
                            try {
                                JSONArray array = new JSONArray(response);
                                JSONObject object = array.getJSONObject(0);
                                float amt = Float.parseFloat(String.valueOf(object.getInt("total")))*100;
                                int r = (int) (amt/100);
                                int p = (int) (amt%100);
                                Log.e("checking",amt + " " + r + " "+ p);
                                rupees.setText(r+".");
                                paise.setText(String.valueOf(p));
                            } catch (JSONException e) {
                                Log.e("checking",e.toString());
                            }

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
                    Map<String,String> params = new HashMap<>();
                    params.put("authorization", "Bearer "+ new SessionData(context).getSessionKey());
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

            return null;
        }
    }
}
