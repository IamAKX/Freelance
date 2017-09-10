package com.akash.applications.freelance.ProfileSettingsOptions;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.akash.applications.freelance.R;
import com.akash.applications.freelance.SetNewPhone;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eyalbira.loadingdots.LoadingDots;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import Connector.ConnectionDetector;
import CustomJSONParser.QualificationParser;
import CustomJSONParser.ServerReply;
import LocalPrefrences.SessionData;
import Utils.Constants;
import Utils.DateTimeManager;
import dmax.dialog.SpotsDialog;

public class EducationDetails extends AppCompatActivity {

    Context context;
    MaterialEditText institute_5_pass,institute_10_pass,institute_12_pass,institute_graduate_pass,institute_postGraduate_pass;
    MaterialEditText board_5_pass,board_10_pass,board_12_pass,board_graduate_pass,board_postGraduate_pass;
    MaterialEditText marks_5_pass,marks_10_pass,marks_12_pass,marks_graduate_pass,marks_postGraduate_pass;
    Spinner highestEducation, yop_5_pass, yop_10_pass, yop_12_pass, yop_graduate_pass, yop_postgraduate_pass;
    Spinner marks_unit_5_pass, marks_unit_10_pass, marks_unit_12_pass, marks_unit_graduate_pass, marks_unit_postgraduate_pass;
    CardView[] cardArray;
    MaterialEditText[] instituteArray, boardArray, marksArray;
    Spinner[] yopArray, marksunitArray;
    ImageView noEducationImageView;
    ScrollView educationScroll;
    LoadingDots loadingDots;
    boolean FIRST_CHECK = false;
    ConnectionDetector detector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_details);
        getSupportActionBar().setTitle("Education");
        context = this;

        initializeComponents();
    }

    private void initializeComponents() {

        detector = new ConnectionDetector(context);

        institute_5_pass = (MaterialEditText) findViewById(R.id.institute_5_pass);
        institute_10_pass = (MaterialEditText) findViewById(R.id.institute_10_pass);
        institute_12_pass = (MaterialEditText) findViewById(R.id.institute_12_pass);
        institute_graduate_pass = (MaterialEditText) findViewById(R.id.institute_graduate_pass);
        institute_postGraduate_pass = (MaterialEditText) findViewById(R.id.institute_postgraduate_pass);

        board_5_pass = (MaterialEditText) findViewById(R.id.board_5_pass);
        board_10_pass = (MaterialEditText) findViewById(R.id.board_10_pass);
        board_12_pass = (MaterialEditText) findViewById(R.id.board_12_pass);
        board_graduate_pass = (MaterialEditText) findViewById(R.id.board_graduate_pass);
        board_postGraduate_pass = (MaterialEditText) findViewById(R.id.board_postgraduate_pass);

        marks_5_pass = (MaterialEditText) findViewById(R.id.mark_5_pass);
        marks_10_pass = (MaterialEditText) findViewById(R.id.mark_10_pass);
        marks_12_pass = (MaterialEditText) findViewById(R.id.mark_12_pass);
        marks_graduate_pass = (MaterialEditText) findViewById(R.id.mark_graduate_pass);
        marks_postGraduate_pass = (MaterialEditText) findViewById(R.id.mark_postgraduate_pass);

        highestEducation = (Spinner) findViewById(R.id.education_Highest_Qualification);

        yop_5_pass = (Spinner) findViewById(R.id.yop_5_passed);
        yop_10_pass = (Spinner) findViewById(R.id.yop_10_passed);
        yop_12_pass = (Spinner) findViewById(R.id.yop_12_passed);
        yop_graduate_pass = (Spinner) findViewById(R.id.yop_graduate_passed);
        yop_postgraduate_pass = (Spinner) findViewById(R.id.yop_postgraduate_passed);

        marks_unit_5_pass = (Spinner) findViewById(R.id.marks_unit_5_pass);
        marks_unit_10_pass = (Spinner) findViewById(R.id.marks_unit_10_pass);
        marks_unit_12_pass = (Spinner) findViewById(R.id.marks_unit_12_pass);
        marks_unit_graduate_pass = (Spinner) findViewById(R.id.marks_unit_graduate_pass);
        marks_unit_postgraduate_pass = (Spinner) findViewById(R.id.marks_unit_postgraduate_pass);

        ArrayAdapter<String> yopAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, DateTimeManager.yearArray());
        yopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yop_5_pass.setAdapter(yopAdapter);
        yop_10_pass.setAdapter(yopAdapter);
        yop_12_pass.setAdapter(yopAdapter);
        yop_graduate_pass.setAdapter(yopAdapter);
        yop_postgraduate_pass.setAdapter(yopAdapter);

        ArrayAdapter<String> markUnitAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, Constants.MARKS_UNIT);
        markUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        marks_unit_5_pass.setAdapter(markUnitAdapter);
        marks_unit_10_pass.setAdapter(markUnitAdapter);
        marks_unit_12_pass.setAdapter(markUnitAdapter);
        marks_unit_graduate_pass.setAdapter(markUnitAdapter);
        marks_unit_postgraduate_pass.setAdapter(markUnitAdapter);

        ArrayAdapter<String> HighestQualificationAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, Constants.HIGHEST_QUALIFICATION_CATEGORY);
        HighestQualificationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        highestEducation.setAdapter(HighestQualificationAdapter);

        noEducationImageView = (ImageView) findViewById(R.id.noeducation_prompt);
        educationScroll = (ScrollView) findViewById(R.id.educatin_scroll);
        loadingDots = (LoadingDots) findViewById(R.id.getEduacationloadingDotProgress);

        cardArray = new CardView[]{(CardView) findViewById(R.id.card_5_pass),(CardView) findViewById(R.id.card_10_pass),(CardView) findViewById(R.id.card_12_pass),(CardView) findViewById(R.id.card_graduate_pass),(CardView) findViewById(R.id.card_post_graduate_pass)};
        instituteArray = new MaterialEditText[]{institute_5_pass,institute_10_pass,institute_12_pass,institute_graduate_pass,institute_postGraduate_pass};
        boardArray = new MaterialEditText[]{board_5_pass,board_10_pass,board_12_pass,board_graduate_pass,board_postGraduate_pass};
        marksArray = new MaterialEditText[]{marks_5_pass,marks_10_pass,marks_12_pass,marks_graduate_pass,marks_postGraduate_pass};
        yopArray = new Spinner[]{yop_5_pass, yop_10_pass, yop_12_pass, yop_graduate_pass, yop_postgraduate_pass};
        marksunitArray = new Spinner[]{marks_unit_5_pass, marks_unit_10_pass, marks_unit_12_pass, marks_unit_graduate_pass, marks_unit_postgraduate_pass};
        educationScroll.setLayoutTransition(new LayoutTransition());
        for(CardView cards : cardArray)
            cards.setLayoutTransition(new LayoutTransition());

        highestEducation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 0:
                        if(FIRST_CHECK)
                            noEducationImageView.setVisibility(View.VISIBLE);
                        educationScroll.setVisibility(View.GONE);
                        for (CardView card : cardArray)
                            card.setVisibility(View.GONE);
                        FIRST_CHECK = true;
                        break;
                    case 1:
                        noEducationImageView.setVisibility(View.GONE);
                        educationScroll.setVisibility(View.VISIBLE);
                        for (CardView card : cardArray)
                            card.setVisibility(View.GONE);
                        for (int i = 0; i < 1; i++)
                            cardArray[i].setVisibility(View.VISIBLE);
                        FIRST_CHECK = true;
                        break;
                    case 2:
                        noEducationImageView.setVisibility(View.GONE);
                        educationScroll.setVisibility(View.VISIBLE);
                        for (CardView card : cardArray)
                            card.setVisibility(View.GONE);
                        for (int i = 0; i < 2; i++)
                            cardArray[i].setVisibility(View.VISIBLE);
                        FIRST_CHECK = true;
                        break;
                    case 3:
                        noEducationImageView.setVisibility(View.GONE);
                        educationScroll.setVisibility(View.VISIBLE);
                        for (CardView card : cardArray)
                            card.setVisibility(View.GONE);
                        for (int i = 0; i < 3; i++)
                            cardArray[i].setVisibility(View.VISIBLE);
                        FIRST_CHECK = true;
                        break;
                    case 4:
                        noEducationImageView.setVisibility(View.GONE);
                        educationScroll.setVisibility(View.VISIBLE);
                        for (CardView card : cardArray)
                            card.setVisibility(View.GONE);
                        for (int i = 0; i < 4; i++)
                            cardArray[i].setVisibility(View.VISIBLE);
                        FIRST_CHECK = true;
                        break;
                    case 5:
                        noEducationImageView.setVisibility(View.GONE);
                        educationScroll.setVisibility(View.VISIBLE);
                        for (CardView card : cardArray)
                            card.setVisibility(View.GONE);
                        for (int i = 0; i < 5; i++)
                            cardArray[i].setVisibility(View.VISIBLE);
                        FIRST_CHECK = true;
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (detector.isConnectingToInternet())
            new LoadQualification().execute();
        else
            detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText())
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        switch (item.getItemId())
        {
            case R.id.menu_item_done:

                if(validateData())
                {
                    if (detector.isConnectingToInternet())
                        new SaveEducationDetails(buildArray()).execute();
                    else
                        detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String buildArray(){
        JSONArray array = new JSONArray();
        String object;

        try {
        switch (highestEducation.getSelectedItemPosition()) {
            case 5:
                object = QualificationParser.makeObject("Post Graduate", institute_postGraduate_pass.getText().toString(), board_postGraduate_pass.getText().toString(), marks_unit_postgraduate_pass.getSelectedItem().toString(), marks_postGraduate_pass.getText().toString(),yop_postgraduate_pass.getSelectedItem().toString());
                array.put(new JSONObject(object));
            case 4:
                object = QualificationParser.makeObject("Graduate", institute_graduate_pass.getText().toString(), board_graduate_pass.getText().toString(), marks_unit_graduate_pass.getSelectedItem().toString(), marks_graduate_pass.getText().toString(),yop_graduate_pass.getSelectedItem().toString());
                array.put(new JSONObject(object));
            case 3:
                object = QualificationParser.makeObject("Higher Secondary", institute_12_pass.getText().toString(), board_12_pass.getText().toString(), marks_unit_12_pass.getSelectedItem().toString(), marks_12_pass.getText().toString(),yop_12_pass.getSelectedItem().toString());
                array.put(new JSONObject(object));
            case 2:
                object = QualificationParser.makeObject("Secondary", institute_10_pass.getText().toString(), board_10_pass.getText().toString(), marks_unit_10_pass.getSelectedItem().toString(), marks_10_pass.getText().toString(),yop_10_pass.getSelectedItem().toString());
                array.put(new JSONObject(object));
            case 1:
                object = QualificationParser.makeObject("Litrate", institute_5_pass.getText().toString(), board_5_pass.getText().toString(), marks_unit_5_pass.getSelectedItem().toString(), marks_5_pass.getText().toString(),yop_5_pass.getSelectedItem().toString());
                array.put(new JSONObject(object));
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return array.toString();
    }

    private boolean validateData() {
        boolean isValid = false;
        switch (highestEducation.getSelectedItemPosition())
        {
            case 0:
                Toast.makeText(context,"You have not mentioned Higher Education",Toast.LENGTH_LONG).show();
                break;
            case 1:
                isValid = checkPrimaryInput();
                break;
            case 2:
                isValid = (checkPrimaryInput() && checkSecondaryInput());
                break;
            case 3:
                isValid = (checkPrimaryInput() && checkSecondaryInput() && checkHigherSecondaryInput());
                break;
            case 4:
                isValid = (checkPrimaryInput() && checkSecondaryInput() && checkHigherSecondaryInput() && checkGraduateInput());
                break;
            case 5:
                isValid = (checkPrimaryInput() && checkSecondaryInput() && checkHigherSecondaryInput() && checkGraduateInput() && checkPostGraduateInput());
                break;
        }
        return isValid;
    }

    private boolean checkPrimaryInput()
    {
        boolean isValid = false;
        if(yop_5_pass.getSelectedItemPosition() == 0)
        {
            Toast.makeText(context,"Select Passing year of your primary education",Toast.LENGTH_LONG).show();
            return isValid;
        }
        if(institute_5_pass.getText().toString().equals(""))
        {
            institute_5_pass.setError("Enter the name of institute");
            return isValid;
        }
        else if(board_5_pass.getText().toString().equals(""))
        {
            board_5_pass.setError("Enter the affiliated board");
            return isValid;
        }
        else if(marks_5_pass.getText().toString().equals(""))
        {
            marks_5_pass.setError("Enter marks obtained");
            return isValid;
        }
        else if (marks_unit_5_pass.getSelectedItemPosition() == 0)
        {
            Toast.makeText(context,"Select unit of obtained marks",Toast.LENGTH_SHORT).show();
            return isValid;
        }

        if(marks_unit_5_pass.getSelectedItemPosition() == 1)
        {
            float marks = Float.parseFloat(marks_5_pass.getText().toString());
            if(marks < 0.0 || marks > 100.0)
            {

                marks_5_pass.setError("Enter valid marks %");
                return isValid;
            }
        }
        else
        if(marks_unit_5_pass.getSelectedItemPosition() == 2)
        {
            float cgpa = Float.parseFloat(marks_5_pass.getText().toString());
            if(cgpa < 0.0 || cgpa > 10.0)
            {
                marks_5_pass.setError("Enter valid CGPA");
                return isValid;
            }
        }
        isValid = true;
        return isValid;
    }
    private boolean checkSecondaryInput()
    {
        boolean isValid = false;
        if(yop_10_pass.getSelectedItemPosition() == 0)
        {
            Toast.makeText(context,"Select Passing year of your secondary education",Toast.LENGTH_LONG).show();
            return isValid;
        }
        if(institute_10_pass.getText().toString().equals(""))
        {
            institute_10_pass.setError("Enter the name of institute");
            return isValid;
        }
        else if(board_10_pass.getText().toString().equals(""))
        {
            board_10_pass.setError("Enter the affiliated board");
            return isValid;
        }
        else if(marks_10_pass.getText().toString().equals(""))
        {
            marks_10_pass.setError("Enter marks obtained");
            return isValid;
        }
        else if (marks_unit_10_pass.getSelectedItemPosition() == 0)
        {
            Toast.makeText(context,"Select unit of obtained marks",Toast.LENGTH_SHORT).show();
            return isValid;
        }

        if(marks_unit_10_pass.getSelectedItemPosition() == 1)
        {
            float marks = Float.parseFloat(marks_10_pass.getText().toString());
            if(marks < 0.0 || marks > 100.0)
            {
                marks_10_pass.setError("Enter valid marks %");
                return isValid;
            }
        }
        else
        if(marks_unit_10_pass.getSelectedItemPosition() == 2)
        {
            float cgpa = Float.parseFloat(marks_10_pass.getText().toString());
            if(cgpa < 0.0 || cgpa > 10.0)
            {
                marks_10_pass.setError("Enter valid CGPA");
                return isValid;
            }
        }
        isValid = true;
        return isValid;
    }
    private boolean checkHigherSecondaryInput()
    {
        boolean isValid = false;
        if(yop_12_pass.getSelectedItemPosition() == 0)
        {
            Toast.makeText(context,"Select Passing year of your Higher Secondary education",Toast.LENGTH_LONG).show();
            return isValid;
        }
        if(institute_12_pass.getText().toString().equals(""))
        {
            institute_12_pass.setError("Enter the name of institute");
            return isValid;
        }
        else if(board_12_pass.getText().toString().equals(""))
        {
            board_12_pass.setError("Enter the affiliated board");
            return isValid;
        }
        else if(marks_12_pass.getText().toString().equals(""))
        {
            marks_12_pass.setError("Enter marks obtained");
            return isValid;
        }
        else if (marks_unit_12_pass.getSelectedItemPosition() == 0)
        {
            Toast.makeText(context,"Select unit of obtained marks",Toast.LENGTH_SHORT).show();
            return isValid;
        }

        if(marks_unit_12_pass.getSelectedItemPosition() == 1)
        {
            float marks = Float.parseFloat(marks_12_pass.getText().toString());
            if(marks < 0.0 || marks > 100.0)
            {
                marks_12_pass.setError("Enter valid marks %");
                return isValid;
            }
        }
        else
        if(marks_unit_12_pass.getSelectedItemPosition() == 2)
        {
            float cgpa = Float.parseFloat(marks_12_pass.getText().toString());
            if(cgpa < 0.0 || cgpa > 10.0)
            {
                marks_12_pass.setError("Enter valid CGPA");
                return isValid;
            }
        }
        isValid = true;
        return isValid;
    }
    private boolean checkGraduateInput()
    {
        boolean isValid = false;
        if(yop_graduate_pass.getSelectedItemPosition() == 0)
        {
            Toast.makeText(context,"Select Passing year of your Graduation",Toast.LENGTH_LONG).show();
            return isValid;
        }
        if(institute_graduate_pass.getText().toString().equals(""))
        {
            institute_graduate_pass.setError("Enter the name of institute");
            return isValid;
        }
        else if(board_graduate_pass.getText().toString().equals(""))
        {
            board_graduate_pass.setError("Enter the affiliated university");
            return isValid;
        }
        else if(marks_graduate_pass.getText().toString().equals(""))
        {
            marks_graduate_pass.setError("Enter marks obtained");
            return isValid;
        }
        else if (marks_unit_graduate_pass.getSelectedItemPosition() == 0)
        {
            Toast.makeText(context,"Select unit of obtained marks",Toast.LENGTH_SHORT).show();
            return isValid;
        }

        if(marks_unit_graduate_pass.getSelectedItemPosition() == 1)
        {
            float marks = Float.parseFloat(marks_graduate_pass.getText().toString());
            if(marks < 0.0 || marks > 100.0)
            {
                marks_graduate_pass.setError("Enter valid marks %");
                return isValid;
            }
        }
        else
        if(marks_unit_graduate_pass.getSelectedItemPosition() == 2)
        {
            float cgpa = Float.parseFloat(marks_graduate_pass.getText().toString());
            if(cgpa < 0.0 || cgpa > 10.0)
            {
                marks_graduate_pass.setError("Enter valid CGPA");
                return isValid;
            }
        }
        isValid = true;
        return isValid;
    }
    private boolean checkPostGraduateInput()
    {
        boolean isValid = false;
        if(yop_postgraduate_pass.getSelectedItemPosition() == 0)
        {
            Toast.makeText(context,"Select Passing year of your Post graduation",Toast.LENGTH_LONG).show();
            return isValid;
        }
        if(institute_postGraduate_pass.getText().toString().equals(""))
        {
            institute_postGraduate_pass.setError("Enter the name of institute");
            return isValid;
        }
        else if(board_postGraduate_pass.getText().toString().equals(""))
        {
            board_postGraduate_pass.setError("Enter the affiliated board");
            return isValid;
        }
        else if(marks_postGraduate_pass.getText().toString().equals(""))
        {
            marks_postGraduate_pass.setError("Enter marks obtained");
            return isValid;
        }
        else if (marks_unit_postgraduate_pass.getSelectedItemPosition() == 0)
        {
            Toast.makeText(context,"Select unit of obtained marks",Toast.LENGTH_SHORT).show();
            return isValid;
        }

        if(marks_unit_postgraduate_pass.getSelectedItemPosition() == 1)
        {
            float marks = Float.parseFloat(marks_postGraduate_pass.getText().toString());
            if(marks < 0.0 || marks > 100.0)
            {
                marks_postGraduate_pass.setError("Enter valid marks %");
                return isValid;
            }
        }
        else
        if(marks_unit_postgraduate_pass.getSelectedItemPosition() == 2)
        {
            float cgpa = Float.parseFloat(marks_postGraduate_pass.getText().toString());
            if(cgpa < 0.0 || cgpa > 10.0)
            {
                marks_postGraduate_pass.setError("Enter valid CGPA");
                return isValid;
            }
        }
        isValid = true;
        return isValid;
    }

    private class SaveEducationDetails extends AsyncTask<String ,String,String>{

        SpotsDialog spotsDialog = new SpotsDialog(context, "Updating your education details");

        String qualificationArray;

        public SaveEducationDetails(String qualificationArray) {
            this.qualificationArray = qualificationArray;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spotsDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SAVE_QUALIFICATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        if(new ServerReply(response).getStatus())
                            Toast.makeText(context,"Details saved successfully.",Toast.LENGTH_SHORT).show();
                        spotsDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        Toast.makeText(context,"Failed to update.",Toast.LENGTH_SHORT).show();
                        spotsDialog.dismiss();
                        Log.i("Checking",new String(error.networkResponse.data));
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
                Log.e("Checking",qualificationArray);
                params.put("qualification",qualificationArray);
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


    private class LoadQualification extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.FETCH_QUALIFICATION,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("checking",response);
                            loadingDots.stopAnimation();
                            loadingDots.setVisibility(View.GONE);
                            //If we are getting success from server
                            JSONArray array = new JSONArray();
                            try {
                                JSONObject object = new JSONObject(response);

                                if(!object.toString().equals("")) {
                                    array = object.getJSONArray("qualification");
                                    if (array.length() > 0) {
                                        {
                                            educationScroll.setVisibility(View.VISIBLE);
                                            fillFetchedCards(array);
                                        }
                                    } else {

                                        noEducationImageView.setVisibility(View.VISIBLE);
                                    }
                                }
                                else
                                {

                                    noEducationImageView.setVisibility(View.VISIBLE);
                                }
                            }
                            catch(JSONException e)
                            {

                                noEducationImageView.setVisibility(View.VISIBLE);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want

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

            };
            //Adding the string request to the queue
            stringRequest.setShouldCache(false);

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.getCache().clear();

            requestQueue.add(stringRequest);

            return null;
        }

        @Override
        protected void onPreExecute() {
            Log.e("Checking","Load Qualifiaction");
            noEducationImageView.setVisibility(View.GONE);
            loadingDots.startAnimation();
            loadingDots.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

    }

    private void fillFetchedCards(JSONArray array) {
        int i;
        QualificationParser parser;
        for(i = array.length()-1; i >= 0; i--)
        {
            try {
                parser = new QualificationParser(array.getJSONObject(i).toString());
                instituteArray[i].setText(parser.getInsName());
                boardArray[i].setText(parser.getBoard());
                marksArray[i].setText(parser.getMarks());
                yopArray[i].setSelection(parser.getYop());
                marksunitArray[i].setSelection(parser.getMarksunit());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        highestEducation.setSelection(array.length());
        for(i = 0; i < array.length(); i++)
            cardArray[i].setVisibility(View.VISIBLE);
    }
}
