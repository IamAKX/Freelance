package com.akash.applications.freelance.ProfileSettingsOptions;

import android.animation.LayoutTransition;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import Connector.ConnectionDetector;
import CustomJSONParser.AddressDetailsParser;
import CustomJSONParser.ServerReply;
import LocalPrefrences.SessionData;
import Utils.Constants;
import dmax.dialog.SpotsDialog;

public class Address extends AppCompatActivity {
    LinearLayout promptLayout, containerLayout;
    Context context;
    Button save;
    Spinner locationType;
    MaterialEditText houseNo,city,pin,state;
    LoadingDots loadingDots;
    ConnectionDetector detector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setTitle("Address");
        context = this;
        detector = new ConnectionDetector(context);
        loadingDots = (LoadingDots) findViewById(R.id.getAddressloadingDotProgress);
        //Fetch address id already stored
        if(detector.isConnectingToInternet())
            new LoadSavedAddress().execute();
        else
            detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");

        promptLayout = (LinearLayout) findViewById(R.id.no_address_prompt);
        containerLayout = (LinearLayout) findViewById(R.id.address_card_container);
//        addCardBtn = (Button) findViewById(R.id.add_address_card);


//        addCardBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showCard();
//                NO_OF_CARDS++;
//                layoutVisibilityController();
//            }
//        });
        promptLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptLayout.setVisibility(View.GONE);
                showCard(0,null,null,null,null);
            }
        });
        containerLayout.setLayoutTransition(new LayoutTransition());
    }

    private void showCard(int locType,String hNo, String cName, String pNo, String sName) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addView = layoutInflater.inflate(R.layout.add_location_card,null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,35);


        locationType = (Spinner) addView.findViewById(R.id.Location_type);
        houseNo = (MaterialEditText) addView.findViewById(R.id.HouseNumber);
        city = (MaterialEditText) addView.findViewById(R.id.City);
        pin = (MaterialEditText) addView.findViewById(R.id.pinCode);
        state = (MaterialEditText) addView.findViewById(R.id.state);
        save = (Button) addView.findViewById(R.id.save_location_card);


        houseNo.setText(hNo);
        city.setText(cName);
        pin.setText(pNo);
        state.setText(sName);



        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, Constants.ADDRESS_CARD_LOCATION_TYPE);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationType.setAdapter(typeAdapter);
        locationType.setSelection(locType);
        locationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 0:
                        houseNo.setEnabled(false);
                        houseNo.setIconLeft(R.color.white);
                        city.setEnabled(false);
                        pin.setEnabled(false);
                        state.setEnabled(false);
                        save.setEnabled(false);
                        save.setBackgroundResource(R.drawable.inactive_button_background);
                        break;
                    case 1:
                        houseNo.setIconLeft(R.drawable.house);
                        houseNo.setEnabled(true);
                        city.setEnabled(true);
                        pin.setEnabled(true);
                        state.setEnabled(true);
                        save.setEnabled(true);
                        save.setBackgroundResource(R.drawable.active_button_background);
                        break;
                    case 2:
                        houseNo.setIconLeft(R.drawable.office);
                        houseNo.setEnabled(true);
                        city.setEnabled(true);
                        pin.setEnabled(true);
                        state.setEnabled(true);
                        save.setEnabled(true);
                        save.setBackgroundResource(R.drawable.active_button_background);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verifyInputs()){
                    if(detector.isConnectingToInternet())
                        new SaveAddress().execute();
                    else
                        detector.showSnackBar(v,"You lack Internet Connectivity");
                }
            }
        });


//        deleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((LinearLayout)addView.getParent()).removeView(addView);
//                NO_OF_CARDS--;
//                layoutVisibilityController();
//            }
//        });

        addView.setLayoutParams(layoutParams);
        containerLayout.addView(addView,0);
    }

    private boolean verifyInputs() {
        boolean isValid = false;
        if(locationType.getSelectedItemPosition()==0)
        {
            Toast.makeText(context,"Select Location type",Toast.LENGTH_SHORT).show();
            return isValid;
        }
        if(houseNo.getText().toString().equals(""))
        {
            houseNo.setError("Enter House number/ Street name ");
            return isValid;
        }
        else if(city.getText().toString().equals(""))
        {
            city.setError("Enter city name ");
            return isValid;
        }
        else if(pin.getText().toString().equals(""))
        {
            pin.setError("Enter Pin number");
            return isValid;
        }
        else if(state.getText().toString().equals(""))
        {
            state.setError("Enter state name");
            return isValid;
        }

        if(pin.getText().length()!=6)
        {
            pin.setError("Pin code is not valid");
            return isValid;
        }
        isValid = true;
        return isValid;
    }


    private class SaveAddress extends AsyncTask<String,String,String> {

        SpotsDialog spotsDialog = new SpotsDialog(context, "Saving your address");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spotsDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SAVE_ADDRESS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            spotsDialog.dismiss();
                            //If we are getting success from server
                            if(new ServerReply(response).getStatus())
                            {
                                Toast.makeText(context,"Address saved",Toast.LENGTH_SHORT).show();
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

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("street", houseNo.getText().toString());
                    params.put("city", city.getText().toString());
                    params.put("pincode", pin.getText().toString());
                    params.put("state", state.getText().toString());
                    params.put("type", String.valueOf(locationType.getSelectedItemPosition()));
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

    private class LoadSavedAddress extends AsyncTask<String ,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDots.setVisibility(View.VISIBLE);
            loadingDots.startAnimation();
        }

        @Override
        protected String doInBackground(String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.FETCH_ADDRESS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            loadingDots.stopAnimation();
                            loadingDots.setVisibility(View.GONE);
                            if(response.equals("{}"))
                            {
                                loadingDots.stopAnimation();
                                loadingDots.setVisibility(View.GONE);
                                promptLayout.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                Log.i("Checking",response);
                                AddressDetailsParser detailsParser = new AddressDetailsParser(response);
                                showCard(detailsParser.getType(),detailsParser.getStreet(),detailsParser.getCity(),detailsParser.getPincode(),detailsParser.getState());
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
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
