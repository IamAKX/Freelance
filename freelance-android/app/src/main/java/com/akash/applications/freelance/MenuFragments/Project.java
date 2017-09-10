package com.akash.applications.freelance.MenuFragments;


import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Adapters.AllProjectListAdapter;
import Connector.ConnectionDetector;
import CustomJSONParser.ServerReply;
import DataModel.AllProjectListModel;
import DataModel.ParticipantForSmallIcons;
import LocalPrefrences.SessionData;
import Utils.Constants;
import Utils.DateFormatManager;

import static com.akash.applications.freelance.Home.context;


/**
 * A simple {@link Fragment} subclass.
 */
public class Project extends Fragment {
    ArrayList<AllProjectListModel> listModels = new ArrayList<>();
    RecyclerView recyclerView;
    LoadingDots loadingDots;
    Context context;
    AllProjectListAdapter adapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = (RecyclerView) getView().findViewById(R.id.projectList);
        loadingDots = (LoadingDots) getView().findViewById(R.id.projectListloadingDots);
        context = getContext();
        loadingDots.setVisibility(View.VISIBLE);
        ConnectionDetector detector = new ConnectionDetector(context);
        if (detector.isConnectingToInternet())
            new FetchAllList().execute();
        else
            detector.showSnackBar(getActivity().getWindow().getDecorView(),"You lack Internet Connectivity");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project, container, false);
    }


    class FetchAllList extends AsyncTask<String,String,String>
    {

        @Override
        protected String doInBackground(final String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.FETCH_ALL_PROJECT_LIST,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.i("Checking", response + " ");
                            try {
                                JSONObject object = new JSONObject(response);
                                JSONArray array = object.getJSONArray("projects");

                                for (int i=0;i<array.length();i++)
                                {
                                    JSONObject obj = array.getJSONObject(i);
                                    JSONArray arrayParticipants = obj.getJSONArray("participant");
                                    ArrayList<ParticipantForSmallIcons> participantForSmallIconses = new ArrayList<>();
                                    for(int j =0; j<arrayParticipants.length(); j++)
                                    {
                                        Log.e("checking",arrayParticipants.length()+" ");
                                        JSONObject userObj = arrayParticipants.getJSONObject(j);
                                        participantForSmallIconses.add(new ParticipantForSmallIcons(userObj.getString("name"),userObj.getString("image"),userObj.getString("_id")));
                                    }
                                    JSONObject duration = obj.getJSONObject("duration");
                                    listModels.add(new AllProjectListModel(obj.getString("_id"),obj.getString("name"),obj.getString("description"),obj.getString("category"), DateFormatManager.utcTODate(duration.getString("deadline"),"dd MMM, yyyy"),obj.getString("projectStatus"),participantForSmallIconses));
                                }
                                setListViewItems();
                                Log.e("checking",listModels.size()+" **");
                            } catch (JSONException e) {
                                Log.e("checking",e.toString());
                            }
                            Log.e("checking",listModels.size()+" ");
                            if(loadingDots.getVisibility() == View.VISIBLE)
                                loadingDots.setVisibility(View.GONE);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                            if(loadingDots.getVisibility() == View.VISIBLE)
                                loadingDots.setVisibility(View.GONE);
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

    private void setListViewItems() {
        Log.e("checking",listModels.size()+" **");
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AllProjectListAdapter(context,listModels);
        recyclerView.setAdapter(adapter);
        recyclerView.invalidate();
        adapter.notifyDataSetChanged();
        if(loadingDots.getVisibility() == View.VISIBLE)
            loadingDots.setVisibility(View.GONE);
    }
}
