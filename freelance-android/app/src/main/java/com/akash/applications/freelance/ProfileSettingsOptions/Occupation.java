package com.akash.applications.freelance.ProfileSettingsOptions;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.SimpleMonthAdapter;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import Connector.ConnectionDetector;
import CustomJSONParser.OccupationDetailParser;
import CustomJSONParser.ServerReply;
import LocalPrefrences.SessionData;
import Utils.Constants;
import Utils.DateTimeManager;
import dmax.dialog.SpotsDialog;

public class Occupation extends AppCompatActivity{

    Context context;
    LinearLayout occupationPrompt, occupationContainer;
    MaterialEditText comapanyName, designation;
    TextView startDate, endDate;
    CheckBox currentWorking;
    DatePickerDialog datePickerDialog;
    Calendar startDateCalendar = Calendar.getInstance();
    Calendar endDateCalender = Calendar.getInstance();
    ConnectionDetector detector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_occupation);
        getSupportActionBar().setTitle("Occupation");
        context = this;

        detector = new ConnectionDetector(context);
        if(detector.isConnectingToInternet())
            new LoadOccupationDetails().execute();
        else
            detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");

        occupationContainer = (LinearLayout) findViewById(R.id.occupationLinearContainer);
        occupationPrompt = (LinearLayout) findViewById(R.id.occupation_prompt);
        occupationPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                occupationPrompt.setVisibility(View.GONE);
                showOccupationCard(null,null,"Start Date","End Date",false);
            }
        });

        occupationContainer.setLayoutTransition(new LayoutTransition());
        occupationPrompt.setLayoutTransition(new LayoutTransition());
    }

    private void showOccupationCard(String orgName, String desgn, String sDate, String eDate, boolean chk) {
        occupationContainer.setVisibility(View.VISIBLE);

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addView = layoutInflater.inflate(R.layout.add_occupation_card,null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,35);

        //Initialize card components
        comapanyName = (MaterialEditText) addView.findViewById(R.id.CompanyName);
        designation =  (MaterialEditText) addView.findViewById(R.id.Desingnation);
        startDate =  (TextView) addView.findViewById(R.id.job_start_date);
        endDate =  (TextView) addView.findViewById(R.id.job_end_date);
        currentWorking = (CheckBox) addView.findViewById(R.id.present_working_checkbox);

        comapanyName.setText(orgName);
        designation.setText(desgn);
        startDate.setText(sDate);
        endDate.setText(eDate);
        currentWorking.setChecked(chk);

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                        startDate.setText(day+"/"+(month+1)+"/"+year);
                        startDateCalendar.set(year,month,day);
                    }
                }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH),false);

                datePickerDialog.setYearRange(DateTimeManager.getCurrentYear()-30,DateTimeManager.getCurrentYear());
                datePickerDialog.setCloseOnSingleTapDay(false);
                datePickerDialog.setVibrate(false);
                datePickerDialog.show(getSupportFragmentManager(),"Starting Date");
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startDate.getText().toString().equalsIgnoreCase("Start Date"))
                {
                    Toast.makeText(context,"Select starting date first",Toast.LENGTH_LONG).show();
                    return;
                }

                String date = startDate.getText().toString();
                String[] dateArray = date.split("/");
                datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                        endDate.setText(day+"/"+(month+1)+"/"+year);
                        endDateCalender.set(year,month,day);

                    }
                }, Integer.parseInt(dateArray[2]), Integer.parseInt(dateArray[1]), Integer.parseInt(dateArray[0]), false);
                datePickerDialog.setYearRange(Integer.parseInt(dateArray[2])-1,DateTimeManager.getCurrentYear());
                datePickerDialog.setCloseOnSingleTapDay(false);
                datePickerDialog.setVibrate(false);
                datePickerDialog.show(getSupportFragmentManager(),"End Date");
            }
        });

        currentWorking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentWorking.isChecked())
                {
                    endDate.setText("  ---  ");
                }
                else
                {
                    endDate.setText("End Date");
                }
            }
        });

        addView.setLayoutParams(layoutParams);
        occupationContainer.addView(addView,0);
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
                //Save Setting
                if(!detector.isConnectingToInternet())
                {
                    detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");
                    return true;
                }
               if(validateInput())
                   new SaveOccupationDetails().execute();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateInput() {
        boolean isValid = false;
        if(comapanyName.getText().toString().equals(""))
        {
            comapanyName.setError("Enter Company/Organization name");
            return isValid;
        }
        else if(designation.getText().toString().equals(""))
        {
            designation.setError("Enter designation/role in "+comapanyName.getText().toString());
            return isValid;
        }
        else if(startDate.getText().toString().equalsIgnoreCase("Start Date"))
        {
            Toast.makeText(context,"Enter joining date",Toast.LENGTH_LONG).show();
            return isValid;
        }

        if(!currentWorking.isChecked()) {
            if(endDate.getText().toString().equalsIgnoreCase("End date")||endDate.getText().toString().equalsIgnoreCase("  ---  "))
            {
                Toast.makeText(context,"Select end date if you are not still working here",Toast.LENGTH_LONG).show();
                return isValid;
            }
            if (startDateCalendar.getTime().after(endDateCalender.getTime())) {
                Toast.makeText(context,"End date must after the start date",Toast.LENGTH_LONG).show();
                return isValid;
            }
            if(endDateCalender.getTime().after(DateTimeManager.getCurrentDate()))
            {
                Toast.makeText(context,"End date cannot be after present date",Toast.LENGTH_LONG).show();
                return isValid;
            }
        }

        isValid = true;
        return isValid;
    }

    class SaveOccupationDetails extends AsyncTask<String,String ,String>{
        SpotsDialog spotsDialog = new SpotsDialog(context, "Saving your address");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spotsDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SAVE_OCCUPATION,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            spotsDialog.dismiss();
                            //If we are getting success from server
                            if(new ServerReply(response).getStatus())
                            {
                                Toast.makeText(context,"Occupation Saved saved",Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                            Toast.makeText(context,"Failed to save, try again",Toast.LENGTH_SHORT).show();
                            spotsDialog.dismiss();
                            NetworkResponse networkResponse = error.networkResponse;

                            Log.i("Checking xx", error.getMessage()+" \n\n"+new String(error.networkResponse.data));
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
                    params.put("position", designation.getText().toString());
                    params.put("organzation", comapanyName.getText().toString());
                    params.put("startDate", startDate.getText().toString());
                    params.put("endDate", endDate.getText().toString());
                    params.put("presentlyWorking", String.valueOf(currentWorking.isChecked()));
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

    private class LoadOccupationDetails extends AsyncTask<String,String ,String>{
        LoadingDots loadingDots;
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            loadingDots = (LoadingDots) findViewById(R.id.getOccupationloadingDotProgress);
            loadingDots.setVisibility(View.VISIBLE);
            loadingDots.startAnimation();
        }

        @Override
        protected String doInBackground(String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.FETCH_OCCUPATION,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking",response);
                            loadingDots.stopAnimation();
                            loadingDots.setVisibility(View.GONE);
                            if(response.equals("{}"))
                            {
                                loadingDots.stopAnimation();
                                loadingDots.setVisibility(View.GONE);
                                occupationPrompt.setVisibility(View.VISIBLE);
                            }
                            else
                            {

                                OccupationDetailParser detailsParser = new OccupationDetailParser(response);
                                Log.i("Checking",detailsParser.getOrganization()+"\n"+detailsParser.getPosition()+"\n"+detailsParser.getStartDate()+"\n"+detailsParser.getEndDate()+"\n"+detailsParser.isPresentlyWorking());
                                showOccupationCard(detailsParser.getOrganization(),detailsParser.getPosition(),detailsParser.getStartDate(),detailsParser.getEndDate(),detailsParser.isPresentlyWorking());
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                            Toast.makeText(context,"Failed to fetch yor details",Toast.LENGTH_SHORT).show();
                            NetworkResponse networkResponse = error.networkResponse;
                            Log.i("Checking", error.getMessage()+" ");
                        }

                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request
                    params.put("authorization", "Bearer "+ new SessionData(context).getSessionKey());

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
