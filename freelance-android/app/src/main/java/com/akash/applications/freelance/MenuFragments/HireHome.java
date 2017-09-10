package com.akash.applications.freelance.MenuFragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.akash.applications.freelance.ProjectPushing.PostProject;
import com.akash.applications.freelance.R;
import com.android.volley.AuthFailureError;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Adapters.EmployeeAdapter;
import Connector.ConnectionDetector;
import CustomJSONParser.WorkerListParser;
import DataModel.EmployeeModel;
import LocalPrefrences.SessionData;
import LocalPrefrences.UserDetails;
import Utils.Constants;
import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

public class HireHome extends Fragment {

    ArrayList<EmployeeModel> list = new ArrayList<>();
    private EmployeeAdapter adapter;
    LoadingDots loadingDots;
    private RecyclerView recyclerView;
    WaveSwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton floatingActionButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hire_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = (RecyclerView)getView().findViewById(R.id.hire_home_recyclerview);
        loadingDots = (LoadingDots) getView().findViewById(R.id.hire_home_loading_dots);
        floatingActionButton = (FloatingActionButton) getView().findViewById(R.id.postProjectFAB);

        final ConnectionDetector detector = new ConnectionDetector(getContext());
        swipeRefreshLayout = (WaveSwipeRefreshLayout) getView().findViewById(R.id.hire_home_swipe_refresh);
        swipeRefreshLayout.setWaveRGBColor(33,33,33);
        swipeRefreshLayout.setColorSchemeColors(Color.WHITE);
        swipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(detector.isConnectingToInternet()) {
                    new FetchEmployee().execute();
                    list.clear();
                    setAdapterInRecyclerView();
                }
                else
                    detector.showSnackBar(getActivity().getWindow().getDecorView(),"You lack Internet Connectivity");

            }
        });
        loadingDots.setVisibility(View.VISIBLE);

        if(detector.isConnectingToInternet())
            new FetchEmployee().execute();
        else
            detector.showSnackBar(getActivity().getWindow().getDecorView(),"You lack Internet Connectivity");
        setAdapterInRecyclerView();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new UserDetails(getContext()).getUserMode().equalsIgnoreCase("Hire"))
                    startActivity(new Intent(getContext(), PostProject.class));
                else
                    Toast.makeText(getContext(),"Switch to HIRE mode before posting project",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setAdapterInRecyclerView() {
//        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new EmployeeAdapter(getContext(),getActivity(),list);
        recyclerView.setAdapter(adapter);
        recyclerView.invalidate();
    }

    private void fillEmployeeArrayList(String response)
    {
        try {
            JSONArray collection = new JSONArray(response);
            for (int i = 0;i<collection.length();i++)
            {
                list.add(new WorkerListParser().getModel((JSONObject) collection.get(i)));
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(loadingDots.getVisibility() == View.VISIBLE)
            loadingDots.setVisibility(View.GONE);
        if(swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    private class FetchEmployee extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {

            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.FETCH_WORKER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("checking",response);
                            fillEmployeeArrayList(response);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
          //                  spotsDialog.dismiss();
                        }

                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request
                    params.put("authorization", "Bearer "+ new SessionData(getContext()).getSessionKey());

                    return params;
                }
            };

            //Adding the string request to the queue
            stringRequest.setShouldCache(false);

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.getCache().clear();

            requestQueue.add(stringRequest);

            return null;
        }
    }
}
