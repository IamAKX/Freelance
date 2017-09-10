package com.akash.applications.freelance.ProfileSettingsOptions;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.akash.applications.freelance.Home;
import com.akash.applications.freelance.ProfileSettings;
import com.akash.applications.freelance.R;
import com.akash.applications.freelance.UserModeManager;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;

import Connector.ConnectionDetector;
import CustomJSONParser.UpdateProfileImageParser;
import LocalPrefrences.OTPSessionHandler;
import LocalPrefrences.SessionData;
import LocalPrefrences.UserDetails;
import Utils.Constants;
import belka.us.androidtoggleswitch.widgets.BaseToggleSwitch;
import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import dmax.dialog.SpotsDialog;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class SelectAppMode extends AppCompatActivity {
    ToggleSwitch toggleSwitch;
    Context context;
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_app_mode);
        getSupportActionBar().setTitle("Application Mode");
        context = this;
        activity = this;
        final ConnectionDetector detector = new ConnectionDetector(context);
        toggleSwitch = (ToggleSwitch) findViewById(R.id.hire_work_toggle);
        toggleSwitch.setCheckedTogglePosition(new UserDetails(context).getUserMode().equalsIgnoreCase("Work")?1:0);
        toggleSwitch.setOnToggleSwitchChangeListener(new BaseToggleSwitch.OnToggleSwitchChangeListener() {
            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                String mode = "";
                switch(position)
                {
                    case 1:
                        mode = "Work";
                        break;
                    case 0:
                        mode = "Hire";
                        break;
                }
                if(detector.isConnectingToInternet())
                    new UserModeManager(context,mode,activity).execute();
                else
                    detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");

            }
        });
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
                startActivity(new Intent(context, ProfileSettings.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
