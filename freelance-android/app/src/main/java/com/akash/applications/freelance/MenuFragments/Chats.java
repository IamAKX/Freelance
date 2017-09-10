package com.akash.applications.freelance.MenuFragments;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
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
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Adapters.ChatListAdapter;
import Connector.ConnectionDetector;
import CustomJSONParser.ChatRoomListParser;
import DataModel.ChatListModel;
import LocalPrefrences.SessionData;
import Utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class Chats extends Fragment {


    ChatListAdapter chatListAdapter;
    ArrayList<ChatListModel> list;
    LoadingDots loadingDots;
    RecyclerView recyclerView;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        list = new ArrayList<>();
        recyclerView = (RecyclerView) getView().findViewById(R.id.chatListrecycler);
        loadingDots = (LoadingDots) getView().findViewById(R.id.ChatlistloadingDots);
        context = getContext();
        loadingDots.setVisibility(View.VISIBLE);
        chatListAdapter = new ChatListAdapter(getActivity(),context,list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setAdapter(chatListAdapter);
        ConnectionDetector detector = new ConnectionDetector(context);
        if(detector.isConnectingToInternet())
            new FetchAllChatRooms().execute();
        else
            detector.showSnackBar(getActivity().getWindow().getDecorView(),"You lack Internet Connectivity");

    }

    class FetchAllChatRooms extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(final String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.FETCH_ALL_CHATROOMS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            if(loadingDots.getVisibility()==View.VISIBLE)
                                loadingDots.setVisibility(View.GONE);
                            Log.i("Checking", response + " ");
                            try {
                                JSONArray array = new JSONArray(response);
                                for(int i = 0;i<array.length();i++)
                                {
                                    ChatRoomListParser parser = new ChatRoomListParser(array.getString(i));
                                    ChatListModel model = new ChatListModel(
                                            parser.getName(),
                                            parser.getImage(),
                                            parser.getChatroomId(),
                                            parser.getUserID(),
                                            parser.getPhone(),
                                            parser.getEmail());
                                    list.add(model);
                                    recyclerView.setAdapter(chatListAdapter);
                                    chatListAdapter.notifyDataSetChanged();
                                    recyclerView.invalidate();
                                    Log.e("checking","Sise of list = "+list.size());
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
                            if(loadingDots.getVisibility()==View.VISIBLE)
                                loadingDots.setVisibility(View.GONE);
                            NetworkResponse networkResponse = error.networkResponse;
                            //////Log.i("Checking", new String(error.networkResponse.data));
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