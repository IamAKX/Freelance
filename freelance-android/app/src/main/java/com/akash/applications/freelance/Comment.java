package com.akash.applications.freelance;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

import Adapters.CommentAdapter;
import CustomJSONParser.ServerReply;
import DataModel.CommentModel;
import LocalPrefrences.SessionData;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import Utils.Constants;



public class Comment extends AppCompatActivity {

    ArrayList<CommentModel> list = new ArrayList<>();
    CommentAdapter adapter;
    ImageView imageView;
    TextView name, rating;
    MaterialRatingBar ratingBar;
    RecyclerView recyclerView;
    Context context;
    String id, uname, urating, imgLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        imageView = (ImageView) findViewById(R.id.userImg);
        name = (TextView) findViewById(R.id.username);
        rating = (TextView) findViewById(R.id.rating);
        ratingBar = (MaterialRatingBar) findViewById(R.id.library_decimal_ratingbar);
        recyclerView = (RecyclerView) findViewById(R.id.commentList);
        context = this;
        id = getIntent().getStringExtra("id");
        uname = getIntent().getStringExtra("name");
        urating = getIntent().getStringExtra("rating");
        imgLink = getIntent().getStringExtra("imgLink");

        Glide.with(this)
                .load(imgLink)
                .placeholder(R.drawable.userdp)
                .error(R.drawable.userdp)
                .bitmapTransform(new CropCircleTransformation(context))
                .into(imageView);

        name.setText(uname);
        rating.setText(urating);
        ratingBar.setRating(Float.parseFloat(urating));

        new FetchComment().execute();

    }

    private class FetchComment extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... params) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.GET_FEEDBACK,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " ");
                            try {
                                JSONArray array = new JSONArray(response);
                                for(int i = 0; i<array.length();i++)
                                {
                                    JSONObject object = array.getJSONObject(i);
                                    CommentModel model = new CommentModel(object.getString("userId"),object.getString("name"),object.getString("image"),object.getString("rating"),object.getString("content"));
                                    list.add(model);
                                }

                            } catch (JSONException e) {
                                Log.e("checking",e.toString()+" ");
                            }
                            if(list.size()>0)
                            {
                                addToAdapert();
                                adapter.notifyDataSetChanged();
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

    private void addToAdapert() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CommentAdapter(context,list);
        recyclerView.setAdapter(adapter);
        recyclerView.invalidate();
    }
}
