package com.akash.applications.freelance;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.eyalbira.loadingdots.LoadingDots;

import java.util.HashMap;
import java.util.Map;

import Connector.ConnectionDetector;
import CustomJSONParser.ServerReply;
import LocalPrefrences.SessionData;
import dmax.dialog.SpotsDialog;
import Utils.Constants;
public class ReportAbuse extends AppCompatActivity {

    String type,field,message;
    EditText msg;
    TextView send;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_abuse);

        msg = (EditText) findViewById(R.id.msg);
        send = (TextView) findViewById(R.id.send);
        type = getIntent().getStringExtra("type");
        field = getIntent().getStringExtra("field");
        context = this;

        getSupportActionBar().setTitle("Report Abuse");
        final ConnectionDetector detector = new ConnectionDetector(context);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(msg.getText().toString().length()==0)
                    return;
                if(detector.isConnectingToInternet())
                {
                    message = msg.getText().toString();
                    new SendReport().execute();
                }
                else
                    detector.showSnackBar(v,"You lack Internet Connection");
            }
        });
    }

    private class SendReport extends AsyncTask<String,String,String>{
        SpotsDialog spotsDialog = new SpotsDialog(context,"Submitting your report");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spotsDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.REPORT_ABUSE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " ");
                            msg.setText("");
                            spotsDialog.dismiss();

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                            spotsDialog.dismiss();
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
                    params.put("type",type);
                    params.put("fileId",field);
                    params.put("content",message);
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
