package com.akash.applications.freelance.MenuFragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akash.applications.freelance.R;
import com.android.volley.AuthFailureError;
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
import Adapters.FavAdapter;
import Connector.ConnectionDetector;
import CustomJSONParser.FavListParser;
import CustomJSONParser.WorkerListParser;
import DataModel.EmployeeModel;
import LocalPrefrences.SessionData;
import Utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class Favourite extends Fragment {

    ArrayList<EmployeeModel> list = new ArrayList<>();
    private FavAdapter adapter;
    LoadingDots loadingDots;
    private RecyclerView recyclerView;
    Context context;
    ConnectionDetector detector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getContext();
        recyclerView = (RecyclerView)getView().findViewById(R.id.hire_home_recyclerview);
        loadingDots = (LoadingDots) getView().findViewById(R.id.hire_home_loading_dots);
        detector = new ConnectionDetector(context);
        if(detector.isConnectingToInternet())
        {
            Log.e("checking"," "+detector.isConnectingToInternet());

        }
        else
            detector.showSnackBar(getActivity().getWindow().getDecorView(),"You lack Internet Connectivity");
        setAdapterInRecyclerView();
        new FetchEmployee().execute();
    }

    private void setAdapterInRecyclerView() {
        //RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FavAdapter(getContext(),getActivity(),list);
        recyclerView.setAdapter(adapter);
        recyclerView.invalidate();
    }

    private void fillEmployeeArrayList(String response)
    {
        try {
            JSONArray collection = new JSONArray(response);
            for (int i = 0;i<collection.length();i++)
            {
                list.add(new FavListParser().getModel((JSONObject) collection.get(i)));
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(loadingDots.getVisibility() == View.VISIBLE)
            loadingDots.setVisibility(View.GONE);

    }

    private class FetchEmployee extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {

            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.FETCH_FAVROURITE,
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
