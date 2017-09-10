package com.akash.applications.freelance;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import Connector.ConnectionDetector;
import LocalPrefrences.SessionData;
import Utils.AppVersionChecker;
import Utils.Constants;
import it.michelelacorte.elasticprogressbar.ElasticDownloadView;
import it.michelelacorte.elasticprogressbar.OptionView;

public class Update extends AppCompatActivity {
    LoadingDots loadingDots;
    ElasticDownloadView elasticDownloadView;
    TextView prompt,updatelist;
    CardView cardView;
    Context context;
    ConnectionDetector detector;
    String currentVersion, fileUrl, totalSize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        OptionView.noBackground = false;
        OptionView.setBackgroundColorSquare(R.color.gray_very_light);
        OptionView.setColorProgressBar(R.color.colorPrimary);
        OptionView.setColorProgressBarInProgress(R.color.blue);
        OptionView.setColorProgressBarText(R.color.colorAccent);
        OptionView.setColorSuccess(R.color.green);
        OptionView.setColorFail(R.color.adctive_red);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        getSupportActionBar().setTitle("App Update");

        context = this;
        detector = new ConnectionDetector(context);

        prompt = (TextView) findViewById(R.id.updatePrompt);
        updatelist = (TextView) findViewById(R.id.updateWhatsNewList);
        cardView = (CardView) findViewById(R.id.card2);
        loadingDots = (LoadingDots) findViewById(R.id.loadingDotProgress);
        elasticDownloadView = (ElasticDownloadView) findViewById(R.id.elastic_download_view);
        cardView.setVisibility(View.GONE);
        prompt.setText("Please wait.. Checking for updates...");

        currentVersion = new AppVersionChecker(context).VERSION_NAME;
        getSupportActionBar().setSubtitle("Current version "+currentVersion);

        if(detector.isConnectingToInternet())
            new CheckUpdate().execute();
        else
            detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");

        elasticDownloadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        downloadFIle();
                    }
                }.start();
            }
        });
    }

    private void downloadFIle() {
        try {
            URL url = new URL("http://coderzheaven.com/sample_folder/sample_file.png");
//            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);

            //connect
            urlConnection.connect();

            //set the path where we want to save the file
            File SDCardRoot = Environment.getExternalStorageDirectory();
            //create a new file, to save the downloaded file
            File file = new File(SDCardRoot,"Freelance.apk");

            FileOutputStream fileOutput = new FileOutputStream(file);

            //Stream used for reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();

            //this is the total size of the file which we are downloading
            final int tSize = urlConnection.getContentLength();
            Log.i("checking",tSize+" ");
            runOnUiThread(new Runnable() {
                public void run() {
                    elasticDownloadView.startIntro();
                    elasticDownloadView.setProgress(0f);
                }
            });

            //create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
                final int downloadedSize = bufferLength;
                // update the progressbar //
                runOnUiThread(new Runnable() {
                    public void run() {
                        float per = ((float)downloadedSize/tSize) * 100;
                        elasticDownloadView.setProgress(per);
                    }
                });
            }
            //close the output stream when complete //
            fileOutput.close();
            runOnUiThread(new Runnable() {
                public void run() {
                    elasticDownloadView.success();
                    // pb.dismiss(); // if you want close it..
                }
            });

        } catch (final MalformedURLException e) {
            showError("Error : MalformedURLException " + e);
            e.printStackTrace();
        } catch (final IOException e) {
            showError("Error : IOException " + e);
            e.printStackTrace();
        }
        catch (final Exception e) {
            showError("Error : Please check your internet connection " + e);
        }
    }

    void showError(final String err){
        Log.e("checking",err);

        runOnUiThread(new Runnable() {
            public void run() {
                elasticDownloadView.fail();
            }
        });
    }


    private class CheckUpdate extends AsyncTask<String,String,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cardView.setVisibility(View.GONE);
            prompt.setText("Please wait.. Checking for updates...");
            elasticDownloadView.setVisibility(View.GONE);
            loadingDots.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.GET_UPLOADED_APP,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(final String response) {
                            //If we are getting success from server
                            try {
                                JSONArray array = new JSONArray(response);
                                JSONObject object = array.getJSONObject(array.length()-1);
                                if(object.getString("version").equalsIgnoreCase(currentVersion))
                                {
                                    prompt.setText("You have the latest version of the app");
                                }
                                else
                                {
                                    prompt.setText("New update is available ("+object.getString("version")+")");
                                    fileUrl = object.getString("link");
                                    totalSize = object.getString("size");
                                    elasticDownloadView.setVisibility(View.VISIBLE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            loadingDots.setVisibility(View.GONE);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                            loadingDots.setVisibility(View.GONE);
                            NetworkResponse networkResponse = error.networkResponse;
                            //Log.i("Checking", new String(error.networkResponse.data));
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