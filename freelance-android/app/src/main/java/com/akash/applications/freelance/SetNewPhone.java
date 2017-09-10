package com.akash.applications.freelance;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mukesh.OtpView;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import Connector.ConnectionDetector;
import CustomJSONParser.ServerReply;
import LocalPrefrences.OTPSessionHandler;
import LocalPrefrences.SessionData;
import dmax.dialog.SpotsDialog;
import swarajsaaj.smscodereader.interfaces.OTPListener;
import Utils.Constants;
import swarajsaaj.smscodereader.receivers.OtpReader;

public class SetNewPhone extends AppCompatActivity implements OTPListener {

    MaterialEditText oldPhone,newPhone;
    OtpView otpView;
    Button change,verify,resendOTP;
    Context context;
    OTPTimerController otpTimerController;
    private String _OTPCODE = "xxxx";
    private boolean _OTPSession = false;
    private boolean _OTPVerified = false;

    ConnectionDetector detector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_phone);

        oldPhone = (MaterialEditText) findViewById(R.id.old_Phone);
        newPhone = (MaterialEditText) findViewById(R.id.new_Phone);
        otpView = (OtpView) findViewById(R.id.change_number_otp_view);
        change = (Button) findViewById(R.id.change_number);
        verify = (Button) findViewById(R.id.change_number_verify_otp);
        resendOTP = (Button) findViewById(R.id.resndOTP);
        context = this;
        OtpReader.bind(this,"NOTICE");
        oldPhone.setText("+91");
        newPhone.setText("+91");
        detector = new ConnectionDetector(context);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ValidateOTP(otpView.getOTP()).execute();
            }
        });

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(SetNewPhone.this, new String[] { android.Manifest.permission.READ_SMS, android.Manifest.permission.RECEIVE_SMS, Manifest.permission.CALL_PHONE },Constants.SMS_GROUP_CODE);
        }

        getSupportActionBar().setTitle("Change Phone Number");
        otpTimerController = new OTPTimerController((1*60*1000),1000);
        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOTP.setEnabled(false);
                resendOTP.setTextColor(context.getResources().getColor(R.color.lighterGray));
                resendOTP.setText("RESEND OTP IN 1:00");
                //Resend OTP Request
                requestForOTP();
                Toast.makeText(context,"Request for resending OTP is accepted",Toast.LENGTH_SHORT).show();
                otpTimerController.start();
            }
        });
        
        oldPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(checkOldPhone() && checkNewPhone())
                {
                    change.setEnabled(true);
                    change.setBackgroundResource(R.drawable.active_button_background);
                }
                else
                {
                    change.setEnabled(false);
                    change.setBackgroundResource(R.drawable.inactive_button_background);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        newPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(checkOldPhone() && checkNewPhone())
                {
                    change.setEnabled(true);
                    change.setBackgroundResource(R.drawable.active_button_background);
                }
                else
                {
                    change.setEnabled(false);
                    change.setBackgroundResource(R.drawable.inactive_button_background);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!detector.isConnectingToInternet())
                {
                    detector.showSnackBar(v,"You lack Internet Connectivity");
                    return;
                }

                findViewById(R.id.new_Phone_Input_Form).setVisibility(View.GONE);
                findViewById(R.id.new_Phone_OTP).setVisibility(View.VISIBLE);
                requestForOTP();
                otpTimerController.start();
            }
        });
    }

    private String requestForOTP() {
        String _OtpNumber = String.format("%04d", new Random().nextInt(10000));
        new OTPSessionHandler(context).setOtpNumber(_OtpNumber);

        final String composedOtpMessage = "Your Frelance mobile verification OTP is "+_OtpNumber+". \n\nThis OTP will expire within 1 minutes.";
        if(!_OTPSession)
        {
            otpTimerController.start();
        }
        Log.i("Checking","I am in requestForOtp");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SMS_ANY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        Log.i("Checking", error + " ");

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put("phoneNumber", newPhone.getText().toString());
                params.put("message",composedOtpMessage);
                params.put("accessKey", Constants.SMS_ANY_API);
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
        if(!_OTPSession)
        {
            Log.i("Checking","Adding to queue");
            requestQueue.add(stringRequest);
            _OTPSession = true;
        }
        return null;
    }

    private boolean checkOldPhone() {
        boolean isValid = false;
        oldPhone.setHelperText("Enter old phone number");
        if(oldPhone.getText().toString().length()==13)
        {
            isValid = true;
            return isValid;
        }
        return isValid;
    }

    private boolean checkNewPhone() {
        boolean isValid = false;
        newPhone.setHelperText("Enter new phone number");
        Log.i("Checking",newPhone.getText().toString().equals(oldPhone.getText().toString())+" ");
        if(newPhone.getText().toString().equals(oldPhone.getText().toString()) && newPhone.getText().toString().length()>0)
        {
            isValid = false;
            newPhone.setHelperText("New phone number cannot be same as old phone number");
            return isValid;
        }
        if(newPhone.getText().toString().length()==13)
        {
            isValid = true;
            return isValid;
        }
        return isValid;
    }

    @Override
    public void otpReceived(String messageText) {
        messageText=messageText.substring(messageText.indexOf("is"));
        messageText=messageText.substring(messageText.indexOf(" ")+1,messageText.indexOf(".")).trim();
        Log.i("Checking","OTP : "+messageText);
        if(_OTPCODE.equals("xxxx"))
            _OTPCODE=messageText;
        if(!_OTPVerified)
        {
            new ValidateOTP(_OTPCODE).execute();
            _OTPVerified = true;
        }
    }

    //-------------------------Re-send OTP Timer Controller------------------------

    private class OTPTimerController extends CountDownTimer {

        public OTPTimerController(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            String min_sec = String.format("%01d:%02d", (TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))),
                    (TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));

            resendOTP.setText("RESEND OTP IN "+min_sec);
        }

        @Override
        public void onFinish() {
            new OTPSessionHandler(context).removeOtpNumber();
            resendOTP.setEnabled(true);
            _OTPSession = false;
            resendOTP.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            resendOTP.setText("RESEND OTP");
        }
    }



    private class ValidateOTP extends AsyncTask<String,String,String> {
        String otp;
        SpotsDialog spotsDialog;
        public ValidateOTP(String otp) {
            _OTPVerified = false;
            this.otp = otp;
            spotsDialog = new SpotsDialog(context, "Verifying your OTP...");

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equalsIgnoreCase("true"))
            {


                spotsDialog.setMessage("Changing your Phone number..");
                requestToChangePhoneNumber();

            }
            else
            {
                spotsDialog.dismiss();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Invalid OTP");
                builder.setMessage("The otp you have enter is not valid. What would you like to do?");
                builder.setPositiveButton("Re-send OTP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        otpTimerController.onFinish();
                        resendOTP.setEnabled(false);
                        resendOTP.setTextColor(context.getResources().getColor(R.color.lighterGray));
                        resendOTP.setText("RESEND OTP IN 1:00");
                        _OTPSession = false;
                        new OTPSessionHandler(context).removeOtpNumber();
                        requestForOTP();

                        _OTPCODE="xxxx";
                        _OTPVerified=false;

                    }
                });
                builder.setNegativeButton("Re-enter OTP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        _OTPCODE="xxxx";
                        _OTPVerified=false;

                    }
                });
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
                //Toast.makeText(context,networkResponse.data.toString(),Toast.LENGTH_LONG).show();
            }
        }

        private void requestToChangePhoneNumber() {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.CHANGE_PHONE_NO,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " ");
                            if(new ServerReply(response).getStatus())
                            {
                                spotsDialog.dismiss();
                                Toast.makeText(context,"Phone number changed successfully", Toast.LENGTH_LONG).show();
                                finish();
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
                    params.put("phone",newPhone.getText().toString());
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

        }

        @Override
        protected String doInBackground(String... params) {


            try {
                Thread.sleep(1500);
                Log.i("Checking",_OTPCODE+"\n"+new OTPSessionHandler(context).getOtpNumber());
                if(_OTPCODE.equals(new OTPSessionHandler(context).getOtpNumber()))
                    return "true";
                else
                    return "false";
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spotsDialog.show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case Constants.SMS_GROUP_CODE:
                if (!(grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        || grantResults[1] == PackageManager.PERMISSION_GRANTED || grantResults[2] == PackageManager.PERMISSION_GRANTED))) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("You have denied Reading SMS Permission. ");
                    builder.setMessage("The OTP will not be verified automatically. Do you want to continue?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
                            {
                                ActivityCompat.requestPermissions(SetNewPhone.this, new String[] { android.Manifest.permission.READ_SMS, android.Manifest.permission.RECEIVE_SMS },Constants.SMS_GROUP_CODE);
                            }
                        }
                    });
                    builder.setCancelable(false);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }


        }

    }

}
