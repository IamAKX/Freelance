package com.akash.applications.freelance.ProfileSettingsOptions;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.akash.applications.freelance.ProfileSettings;
import com.akash.applications.freelance.R;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dpizarro.autolabel.library.AutoLabelUI;
import com.dpizarro.autolabel.library.AutoLabelUISettings;
import com.dpizarro.autolabel.library.Label;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Connector.ConnectionDetector;
import CustomJSONParser.ServerReply;
import LocalPrefrences.PreferenceKey;
import LocalPrefrences.SessionData;
import LocalPrefrences.UserDetails;
import Utils.Constants;
import cn.refactor.kmpautotextview.KMPAutoComplTextView;
import dmax.dialog.SpotsDialog;

public class SelectCategory extends AppCompatActivity {

    Context context;
    AutoLabelUI mAutoLabel;
    List<String> selectedlist = new ArrayList<String>();
    ConnectionDetector detector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setTitle("Category");
        context = this;

        detector = new ConnectionDetector(context);

        //Building the auto lebel tag settings
        AutoLabelUISettings autoLabelUISettings =
                new AutoLabelUISettings.Builder()
                        .withBackgroundResource(R.color.default_background_label)
                        .withIconCross(R.drawable.cross)
                        .withMaxLabels(10)
                        .withShowCross(true)
                        .withLabelsClickables(true)
                        .withTextColor(android.R.color.white)
                        .withTextSize(R.dimen.label_title_size)
                        .withLabelPadding(30)
                        .build();

        mAutoLabel = (AutoLabelUI) findViewById(R.id.label_view);
        mAutoLabel.setSettings(autoLabelUISettings);

        initializeData();

        final KMPAutoComplTextView complTextView = (KMPAutoComplTextView) findViewById(R.id.tvAutoCompl);
        complTextView.setDatas(Constants.categoryList());
        complTextView.setOnPopupItemClickListener(new KMPAutoComplTextView.OnPopupItemClickListener() {
            @Override
            public void onPopupItemClick(CharSequence charSequence) {
                if(selectedlist.contains(charSequence.toString()))
                    Toast.makeText(context, "You have already selected "+charSequence.toString(), Toast.LENGTH_SHORT).show();
                else
                    if(!mAutoLabel.addLabel(charSequence.toString()))
                        Toast.makeText(context, "You can select atmost 10 category.", Toast.LENGTH_SHORT).show();
                    else
                    {
                        selectedlist.add(charSequence.toString());
                        findViewById(R.id.categoryPrompt).setVisibility(View.GONE);
                    }

                complTextView.setText(null);
            }
        });

       mAutoLabel.setOnLabelsEmptyListener(new AutoLabelUI.OnLabelsEmptyListener() {
           @Override
           public void onLabelsEmpty() {
               findViewById(R.id.categoryPrompt).setVisibility(View.VISIBLE);
           }
       });

       mAutoLabel.setOnRemoveLabelListener(new AutoLabelUI.OnRemoveLabelListener() {
           @Override
           public void onRemoveLabel(View view, int position) {
               Label removedLabel = (Label)view;
               Log.i("Checking",removedLabel.getText());
               selectedlist.remove(removedLabel.getText());
           }

       });
    }

    private void initializeData() {
        String setCategories = new UserDetails(context).getUserCategory();
        if(!setCategories.equalsIgnoreCase(PreferenceKey.NO_KEY_FOUND))
        {
            String[] catArray = setCategories.substring(1,setCategories.indexOf(']')).split(",");
            if(catArray.length > 0)
                findViewById(R.id.categoryPrompt).setVisibility(View.GONE);
            for(String s : catArray)
            {
                selectedlist.add(s);
                mAutoLabel.addLabel(s);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menu_item_done:
                if(!detector.isConnectingToInternet())
                {
                    detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");
                    return true;
                }

                //Save Setting
                new UserDetails(context).setUserCategory(new JSONArray(selectedlist).toString().replace("\"",""));

                new SaveCategory(new JSONArray(selectedlist).toString().replace("\"","")).execute();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class SaveCategory extends AsyncTask<String,String,String>{
        SpotsDialog spotsDialog = new SpotsDialog(context,"Updating category, please wait...");
        String categoryArray;

        public SaveCategory(String categoryArray) {
            this.categoryArray = categoryArray;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spotsDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SAVE_CATEGORY,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //If we are getting success from server
                                spotsDialog.dismiss();
                                if(new ServerReply(response).getStatus())
                                {
                                    startActivity(new Intent(context, ProfileSettings.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
                                }
                                else
                                {
                                    Toast.makeText(context,"Failed to save your category at sever",Toast.LENGTH_LONG).show();
                                }

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

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        //Adding parameters to request
                        params.put("category",categoryArray);
                        return params;
                    }

                };
                //Adding the string request to the queue
                stringRequest.setShouldCache(false);

                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.getCache().clear();

                requestQueue.add(stringRequest);


            return null;
        }
    }
}
