package com.akash.applications.freelance.ProjectPushing;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.fourmob.datetimepicker.date.DatePickerDialog;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Connector.ConnectionDetector;
import CustomJSONParser.ServerReply;
import LocalPrefrences.SessionData;
import Utils.Constants;
import Utils.DateFormatManager;
import Utils.DateTimeManager;
import dmax.dialog.SpotsDialog;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ProjectAgreement extends AppCompatActivity {

    ImageView userImage;
    TextView userName, projectName, startDate, endDate, hireNselectBtn;
    EditText editText;
    CheckBox checkBox;
    MaterialRatingBar ratingBar;
    DatePickerDialog datePickerDialog;
    String startingUTC, endUTC;
    Date startD, endD;
    Context context;
    ConnectionDetector detector;

    String workerID,pID,pName,uImage,uName,chatroomId;
    float rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_agreement);

        context = this;

        getDataFromIntent();
        detector = new ConnectionDetector(context);
        getSupportActionBar().setTitle("Project Agreement");
        userImage = (ImageView) findViewById(R.id.userDP);
        userName = (TextView) findViewById(R.id.name);
        startDate = (TextView) findViewById(R.id.dateOfStart);
        endDate = (TextView) findViewById(R.id.dateOfEnd);
        hireNselectBtn = (TextView) findViewById(R.id.finalHire);
        projectName = (TextView) findViewById(R.id.projName);
        editText = (EditText) findViewById(R.id.amount);
        checkBox = (CheckBox) findViewById(R.id.agreeChkBox);
        ratingBar = (MaterialRatingBar) findViewById(R.id.library_decimal_ratingbar);

        hireNselectBtn.setEnabled(false);
        hireNselectBtn.setBackgroundResource(R.drawable.inactive_button_background);
        Glide.with(context)
                .load(Constants.USER_CURRENT_PROFILE_IMAGE+uImage)
                .placeholder(R.drawable.userdp)
                .bitmapTransform(new CropCircleTransformation(context))
                .error(R.drawable.userdp)
                .into(userImage);

        userName.setText(uName);
        ratingBar.setRating(rating);
        projectName.setText(pName);


        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                        startingUTC = DateFormatManager.dateToUTC(day+"/"+(month+1)+"/"+year);
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.YEAR,year);
                        c.set(Calendar.MONTH,month);
                        c.set(Calendar.DAY_OF_MONTH,day);
                        startD = c.getTime();
                        startDate.setText(DateFormatManager.dateFormatter(startD, "dd MMM, yyyy"));
                    }
                }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH),false);

                datePickerDialog.setYearRange(DateTimeManager.getCurrentYear(),DateTimeManager.getCurrentYear()+2);
                datePickerDialog.setCloseOnSingleTapDay(false);
                datePickerDialog.setVibrate(false);
                datePickerDialog.show(getSupportFragmentManager(),"Commencement Date");
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                        Log.e("checking",year+" - "+month+" - "+day);
                        endUTC = DateFormatManager.dateToUTC(day+"/"+(month+1)+"/"+year);
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.YEAR,year);
                        c.set(Calendar.MONTH,month);
                        c.set(Calendar.DAY_OF_MONTH,day);
                        endD = c.getTime();
                        endDate.setText(DateFormatManager.dateFormatter(endD, "dd MMM, yyyy"));
                        Log.e("checking",endUTC+" "+endD.toString());
                    }
                }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH),false);

                datePickerDialog.setYearRange(DateTimeManager.getCurrentYear(),DateTimeManager.getCurrentYear()+2);
                datePickerDialog.setCloseOnSingleTapDay(false);
//                if(startD != null)
//                    datePickerDialog.onDayOfMonthSelected(startD.getYear(),startD.getMonth(),startD.getDay());
                datePickerDialog.setVibrate(false);
                datePickerDialog.show(getSupportFragmentManager(),"Accomplishment Date");
            }
        });

        hireNselectBtn.setEnabled(false);
        hireNselectBtn.setBackgroundResource(R.drawable.active_button_background);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    hireNselectBtn.setEnabled(true);
                    hireNselectBtn.setBackgroundResource(R.drawable.active_button_background);
                }
                else
                {
                    hireNselectBtn.setEnabled(false);
                    hireNselectBtn.setBackgroundResource(R.drawable.inactive_button_background);
                }
            }
        });

        hireNselectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!detector.isConnectingToInternet())
                {
                    detector.showSnackBar(v,"You lack Internet connection");
                    return;
                }

                if(validateAgreement())
                {
                    //Send aggreement
                    new Agree().execute();
                }
            }
        });
    }

    private void getDataFromIntent() {

        workerID = getIntent().getStringExtra("workerID");
        pID = getIntent().getStringExtra("projectId");
        pName = getIntent().getStringExtra("projectName");
        uImage = getIntent().getStringExtra("uImage");
        uName = getIntent().getStringExtra("uName").toUpperCase();
        rating = Float.parseFloat(getIntent().getStringExtra("ratings"));
        chatroomId = getIntent().getStringExtra("chatroomId");
    }

    public boolean validateAgreement()
    {
        if(startD == null)
        {
            Toast.makeText(context,"Select Commencement date",Toast.LENGTH_LONG).show();
            return false;
        }

        if(startD == null)
        {
            Toast.makeText(context,"Select Commencement date",Toast.LENGTH_LONG).show();
            return false;
        }

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,0);
        c.set(Calendar.MONTH,0);
        c.set(Calendar.DAY_OF_MONTH,0);
        Date today = c.getTime();

        if(startD.before(today))
        {
            Toast.makeText(context,"This Commencement date will never come",Toast.LENGTH_LONG).show();
            return false;
        }

        if(endD.before(startD))
        {
            Toast.makeText(context,"Date of Commencement should be before Date of Accomplishment",Toast.LENGTH_LONG).show();
            return false;
        }

        if(editText.getText().toString().length()==0)
        {
            Toast.makeText(context,"Enter cost of project",Toast.LENGTH_LONG).show();
            return false;
        }

        if(!checkBox.isChecked())
        {
            Toast.makeText(context,"Accept the agreement",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    private class Agree extends AsyncTask<String,String,String>{

        SpotsDialog spotsDialog = new SpotsDialog(context, "Hiring "+uName+", Please wait");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spotsDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.PROJECT_AGREEMENT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " ");
                            if(new ServerReply(response).getStatus())
                            {
                                context.startActivity(new Intent(context, PostedProjectDetails.class).putExtra("id",pID).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
                            }
                            spotsDialog.dismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                            spotsDialog.dismiss();
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
                    params.put("employeeId",workerID);
                    params.put("cDate",startingUTC);
                    params.put("aDate",endUTC);
                    params.put("projectId",pID);
                    params.put("amount",editText.getText().toString());
                    params.put("chatroomId",chatroomId);
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
