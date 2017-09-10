package com.akash.applications.freelance.ProjectPushing;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.google.firebase.database.DatabaseReference;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Slider;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import Connector.ConnectionDetector;
import CustomJSONParser.ServerReply;
import LocalPrefrences.SessionData;
import Utils.Constants;
import Utils.DateFormatManager;
import Utils.DateTimeManager;
import cn.refactor.kmpautotextview.KMPAutoComplTextView;
import dmax.dialog.SpotsDialog;

import static com.akash.applications.freelance.ProjectPushing.UserSelection.selectUsersList;

public class PostProject extends AppCompatActivity {

    Slider responseTime;
    MaterialEditText projectName;
    MaterialEditText description;
    EditText start,end;
    TextView deadline, timeoutimeTV;
    Spinner rangeUnit,responseTimeUnit;
    KMPAutoComplTextView autoComplTextView;
    DatePickerDialog datePickerDialog;
    Calendar startDateCalendar = Calendar.getInstance();
    Context context;
    String dateInUTC;
    long timeout = 1;
    boolean singleUser = false;
    private String wID = "";
    ConnectionDetector detector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_project);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = this;
        detector = new ConnectionDetector(context);
        projectName=(MaterialEditText) findViewById(R.id.post_project_shortName);
        description = (MaterialEditText) findViewById(R.id.post_project_details);
        deadline = (TextView) findViewById(R.id.post_project_deadline);
        rangeUnit = (Spinner) findViewById(R.id.post_project_spinner);
        responseTimeUnit = (Spinner) findViewById(R.id.post_project_spinner2);
        autoComplTextView = (KMPAutoComplTextView) findViewById(R.id.post_project_tvAutoCompl);
        start = (EditText) findViewById(R.id.post_prject_startlimit);
        end = (EditText) findViewById(R.id.post_prject_endlimit);
        responseTime = (Slider) findViewById(R.id.responseTimeSlider);
        timeoutimeTV = (TextView) findViewById(R.id.timeoutTime);

        getSupportActionBar().setTitle("Post Project");
        Log.e("checking","data --------------"+Constants.data.size());
        autoComplTextView.setDatas(Constants.categoryList());
        setRangeUnitSpinner();

        responseTime.setPosition(5.0F,true);
        responseTime.setValueRange(0,59,true);
        responseTime.setValue(5.0F,true);
        deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                        deadline.setText(day+"/"+(month+1)+"/"+year);
                        startDateCalendar.set(year,month,day);
                        dateInUTC = DateFormatManager.dateToUTC(day+"/"+(month+1)+"/"+year);
                        Log.i("checking",dateInUTC);

                    }
                }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH),false);

                datePickerDialog.setYearRange(DateTimeManager.getCurrentYear(),DateTimeManager.getCurrentYear()+2);
                datePickerDialog.setCloseOnSingleTapDay(false);
                datePickerDialog.setVibrate(false);
                datePickerDialog.show(getSupportFragmentManager(),"Deadline");
            }
        });

        responseTime.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {
            @Override
            public void onPositionChanged(Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {
                timeoutimeTV.setText(String.valueOf(newValue)+" "+responseTimeUnit.getSelectedItem().toString());
            }
        });

        responseTimeUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 0:
                        timeout = 60*1000;
                        responseTime.setValue(1.0F,true);
                        responseTime.setValueRange(0,59,true);
                        break;

                    case 1:
                        timeout = 60*60*1000;
                        responseTime.setValue(1.0F,true);
                        responseTime.setValueRange(0,23,true);
                        break;

                    case 2:
                        timeout = 24*60*60*1000;
                        responseTime.setValue(1.0F,true);
                        responseTime.setValueRange(0,30,true);
                        break;

                    case 3:
                        timeout = 7*24*60*60*1000;
                        responseTime.setValue(1.0F,true);
                        responseTime.setValueRange(0,4,true);
                        break;

                    case 4:
                        timeout = 30*24*60*60*1000;
                        responseTime.setValue(1.0F,true);
                        responseTime.setValueRange(0,12,true);
                        break;
                }
                timeoutimeTV.setText(String.valueOf(responseTime.getValue())+" "+responseTimeUnit.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("wID")) {
                wID = extras.getString("wID");
                getSupportActionBar().setSubtitle(extras.getString("wNAME").toUpperCase());
                singleUser = true;
            }
        }
    }

    private void setRangeUnitSpinner()
    {
        String[] units = {"Hours","Days","Weeks","Months"};
        String[] units2 = {"Minutes","Hours","Days","Weeks","Months"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, units);
        rangeUnit.setAdapter(adapter);
        adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, units2);
        responseTimeUnit.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(singleUser)
            getMenuInflater().inflate(R.menu.menu_post_project_single_worker, menu);
        else
            getMenuInflater().inflate(R.menu.menu_post_project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_item_cancel:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Alert!");
                builder.setMessage("You are going to close project posting process, which will result in loss of project details (if any).Proceed?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;

            case R.id.menu_item_next:
                //Save Setting
                if(validateInput()) {

                    startActivity(new Intent(context, UserSelection.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("pName",projectName.getText().toString())
                    .putExtra("pDesc", description.getText().toString())
                    .putExtra("pCategory",autoComplTextView.getText().toString())
                    .putExtra("pStart",start.getText().toString())
                    .putExtra("pEnd",end.getText().toString())
                    .putExtra("pUnit",rangeUnit.getSelectedItem().toString())
                    .putExtra("pDeadline",dateInUTC)
                    .putExtra("timeout",String.valueOf(responseTime.getValue()*timeout)));
                    finish();
                }

                return true;

            case R.id.menu_item_post:
                if (!detector.isConnectingToInternet())
                {
                    detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");
                    return true;
                }
                if(validateInput())
                    new PushJobToSingleWorker().execute();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateInput() {
        if(projectName.getText().toString().equals(""))
        {
            projectName.setError("Enter project name");
            return false;
        }
        if(description.getText().toString().equals(""))
        {
            description.setError("Enter description");
            return false;
        }
        if(autoComplTextView.getText().toString().equals(""))
        {
            autoComplTextView.setError("Select category");
            return false;
        }

        if(responseTime.getValue()<1)
        {
            Toast.makeText(context,"Select minimu response time",Toast.LENGTH_LONG).show();
            return false;
        }


        if(!Constants.categoryList().contains(autoComplTextView.getText().toString()))
        {
            Toast.makeText(context,"Enter a category from suggestion list",Toast.LENGTH_LONG).show();
            return false;
        }
        if(start.getText().toString().equals(""))
        {
            start.setError("Enter start date");
            return false;
        }
        if(end.getText().toString().equals("")) {
            end.setError("Enter end date");
            return false;
        }
        int s = Integer.parseInt(start.getText().toString());
        int e = Integer.parseInt(end.getText().toString());
        if(s > e)
        {
            start.setError("Start range should be less than end range");
            end.setError("Start range should be less than end range");
            return false;
        }
        if(deadline.getText().toString().equalsIgnoreCase("End Date"))
        {
            deadline.setError("Enter deadline");
            return false;
        }

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,0);
        c.set(Calendar.MONTH,0);
        c.set(Calendar.DAY_OF_MONTH,0);
        Date today = c.getTime();
        String[] setDate = deadline.getText().toString().split("/");
        c.set(Calendar.YEAR,Integer.parseInt(setDate[0]));
        c.set(Calendar.MONTH,Integer.parseInt(setDate[1]));
        c.set(Calendar.DAY_OF_MONTH,Integer.parseInt(setDate[2]));
        Date dline = c.getTime();


        if(!dline.before(today))
        {
            Toast.makeText(context,"Selected deadline is not valid.",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private class PushJobToSingleWorker extends AsyncTask<String,String,String>{
        SpotsDialog spotsDialog = new SpotsDialog(context,"Posting project to "+getSupportActionBar().getSubtitle());
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
                                spotsDialog.dismiss();
                                Toast.makeText(context,"Your project was posted successfully",Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else {

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                            String s = new String(error.networkResponse.data);
                            spotsDialog.dismiss();
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
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request
                    params.put("name",projectName.getText().toString());
                    params.put("description",description.getText().toString());
                    params.put("startDate",start.getText().toString());
                    params.put("endDate",end.getText().toString());
                    params.put("type",rangeUnit.getSelectedItem().toString().toLowerCase());
                    params.put("category",autoComplTextView.getText().toString());
                    params.put("participant",wID);
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
