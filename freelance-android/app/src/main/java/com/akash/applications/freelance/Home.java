package com.akash.applications.freelance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


import com.akash.applications.freelance.MenuFragments.Chats;
import com.akash.applications.freelance.MenuFragments.Dashboard;
import com.akash.applications.freelance.MenuFragments.Favourite;
import com.akash.applications.freelance.MenuFragments.HireHome;
import com.akash.applications.freelance.MenuFragments.Notification;
import com.akash.applications.freelance.MenuFragments.Project;
import com.akash.applications.freelance.MenuFragments.WorkerHome;
import com.akash.applications.freelance.ProfileSettingsOptions.SelectCategory;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.rm.rmswitch.RMTristateSwitch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import LocalPrefrences.SessionData;
import LocalPrefrences.UserDetails;
import Utils.Constants;
import dmax.dialog.SpotsDialog;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static Context context;
    private View navHeaderView;
    public static ImageView userProfilePicture;
    private TextView userName, userEmail, notificationBadgeCount, chatBadgeCount, appModeTextDisplay;
    RMTristateSwitch appModeToggleSwitch;
    static Menu navMenu;
    FragmentManager fragmentManager;
    private int prevState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        fragmentManager = getSupportFragmentManager();

        updateTokenToServer(FirebaseInstanceId.getInstance().getToken());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navMenu = navigationView.getMenu();
        startService(new Intent(context,FBInstanceIDService.class));
        startService(new Intent(context,FBMessagingService.class));
        Log.i("checking",FirebaseInstanceId.getInstance().getToken()+" ");
        navHeaderView = navigationView.inflateHeaderView(R.layout.nav_header_home);
        //Initialize navigation drawer display components
        userProfilePicture = (ImageView)navHeaderView.findViewById(R.id.userProfilePicture);
        userName = (TextView)navHeaderView.findViewById(R.id.userName);
        userEmail = (TextView)navHeaderView.findViewById(R.id.userEmail);
        appModeToggleSwitch = (RMTristateSwitch) navHeaderView.findViewById(R.id.rm_triswitch_work_hire);
        appModeTextDisplay = (TextView) navHeaderView.findViewById(R.id.app_mode_text);

        //Initialize Notification Badges
        notificationBadgeCount = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.Alerts));
        notificationBadgeCount.setTypeface(null, Typeface.BOLD);
        notificationBadgeCount.setBackgroundResource(R.drawable.badge_background);
        notificationBadgeCount.setGravity(Gravity.CENTER);
        notificationBadgeCount.setLayoutParams(new ActionBar.LayoutParams(70,70));
        notificationBadgeCount.setPadding(10,10,10,10);
        notificationBadgeCount.setTextColor(getResources().getColor(R.color.white));

        chatBadgeCount = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.Chat));
        chatBadgeCount.setTypeface(null, Typeface.BOLD);
        chatBadgeCount.setBackgroundResource(R.drawable.badge_background);
        chatBadgeCount.setGravity(Gravity.CENTER);
        chatBadgeCount.setLayoutParams(new ActionBar.LayoutParams(70,70));
        chatBadgeCount.setPadding(10,10,10,10);
        chatBadgeCount.setTextColor(getResources().getColor(R.color.white));

        notificationBadgeCount.setText("80");
        chatBadgeCount.setText("99+");

        notificationBadgeCount.setVisibility(View.GONE);
        chatBadgeCount.setVisibility(View.GONE);
        //Display User image, name, email
        Glide.with(context)
                .load(new UserDetails(context).getUserImage())
                .bitmapTransform(new CropCircleTransformation(context))
                .placeholder(R.drawable.userdp)
                .error(R.drawable.userdp)
                .into(userProfilePicture);

        userName.setText(new UserDetails(context).getUserName());
        userEmail.setText(new UserDetails(context).getUserEmail());
        initModeToggleSwitich();

        //Making default opening fragment as Home

        if(new UserDetails(context).getUserMode().equalsIgnoreCase("Work")) {
            changeFragment(new WorkerHome());
        }
        else {
            changeFragment(new HireHome());
        }

        Intent intent = getIntent();

        // Get the extras (if there are any)
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("open")) {
                String launch = extras.getString("open");

                switch (launch)
                {
                    case "category":
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                        builder.setTitle("Welcome to Freelance");
                        builder.setMessage("Please select the category of Freelancers so that we can fetch best suited suggestions.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                                //Launch email client app
                                startActivity(new Intent(context, SelectCategory.class));
                            }
                        });
                        builder.setCancelable(false);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        break;
                    case "project":
                        changeFragment(new Project());
                        break;

                }

            }
        }

        prevState = RMTristateSwitch.STATE_MIDDLE;

        appModeToggleSwitch.addSwitchObserver(new RMTristateSwitch.RMTristateSwitchObserver() {
            @Override
            public void onCheckStateChange(RMTristateSwitch switchView, int state) {
                switch(state)
                {
                    case RMTristateSwitch.STATE_LEFT:
                        appModeTextDisplay.setText("HIRE");
                        prevState = RMTristateSwitch.STATE_LEFT;
                        new UserModeManager(context,"Hire").execute();
                        changeFragment(new HireHome());
                        break;
                    case RMTristateSwitch.STATE_MIDDLE:
                        if(prevState == RMTristateSwitch.STATE_LEFT)
                        {
                            prevState = RMTristateSwitch.STATE_RIGHT;
                            appModeToggleSwitch.setState(prevState);
                            appModeTextDisplay.setText("WORK");
                            changeFragment(new WorkerHome());
                            new UserModeManager(context,"Work").execute();
                        }
                        else
                        {
                            prevState = RMTristateSwitch.STATE_LEFT;
                            appModeTextDisplay.setText("HIRE");
                            changeFragment(new HireHome());
                            appModeToggleSwitch.setState(prevState);
                        }
                        break;
                    case RMTristateSwitch.STATE_RIGHT:
                        prevState = RMTristateSwitch.STATE_RIGHT;
                        appModeTextDisplay.setText("WORK");
                        changeFragment(new WorkerHome());
                        break;
                }

            }
        });
    }

    private void initModeToggleSwitich() {
        int position = 0;
        String s = new UserDetails(context).getUserMode();
        if(s.equalsIgnoreCase("Work"))
        {
            navMenu.findItem(R.id.PostProject).setVisible(false);
            navMenu.findItem(R.id.Notification).setVisible(true);
            position = RMTristateSwitch.STATE_RIGHT;
            appModeTextDisplay.setText("WORK");
            appModeToggleSwitch.setState(position);
        }
        else if(s.equalsIgnoreCase("Hire"))
        {
            navMenu.findItem(R.id.PostProject).setVisible(true);
            navMenu.findItem(R.id.Notification).setVisible(false);
            position = RMTristateSwitch.STATE_LEFT;
            appModeTextDisplay.setText("HIRE");
            appModeToggleSwitch.setState(position);
        }
        else
        {
            navMenu.findItem(R.id.PostProject).setVisible(false);
            navMenu.findItem(R.id.Notification).setVisible(false);
            position = RMTristateSwitch.STATE_MIDDLE;
            appModeTextDisplay.setText("NONE");
            appModeToggleSwitch.setState(position);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the WorkerHome/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        int id = item.getItemId();

        switch(id)
        {
            case R.id.Home:
                if(new UserDetails(context).getUserMode().equalsIgnoreCase("Work"))
                    fragment = new WorkerHome();
                else
                    fragment = new HireHome();

                getSupportActionBar().setTitle("Home");
                changeFragment(fragment);
                break;

//            case R.id.Dashboard:
//                fragment = new Dashboard();
//                getSupportActionBar().setTitle("Dashboard");
//                changeFragment(fragment);
//                break;

            case R.id.PostProject:
                fragment = new Project();
                getSupportActionBar().setTitle("Project");
                changeFragment(fragment);
                break;

            case R.id.Notification:
                fragment = new Notification();
                getSupportActionBar().setTitle("Jobs");
                changeFragment(fragment);
//                startActivity(new Intent(context, ViewProject.class));
                break;

            case R.id.Chat:
                fragment = new Chats();
                getSupportActionBar().setTitle("Chat");
                changeFragment(fragment);
                break;

            case R.id.Favourite:
                fragment = new Favourite();
                getSupportActionBar().setTitle("Favourite");
                changeFragment(fragment);
                break;

            case R.id.Alerts:
                fragment = new Notification();
                getSupportActionBar().setTitle("Notification");
                changeFragment(fragment);
                break;

            case R.id.Account:
                startActivity(new Intent(context,Account.class));
                break;

            case R.id.ProfileSettings:
                startActivity(new Intent(context,ProfileSettings.class));
                break;

            case R.id.Share:
                new GetAPKLink().execute();
                break;

            case R.id.Logout:
                new SessionData(context).removeSessionKey();
                new UserDetails(context).removeUserDetails();
                startActivity(new Intent(context,SignIn.class));
                finish();
                break;
            case R.id.About:
                startActivity(new Intent(context,About.class));
                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.container,fragment)
                .commit();
    }

    private void updateTokenToServer(final String newToken) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.FCM_TOKEN_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        Log.i("Checking", response + " ");

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want

                        NetworkResponse networkResponse = error.networkResponse;
                       // //Log.i("Checking", new String(error.networkResponse.data));
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
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put("authorization", "Bearer "+ new SessionData(context).getSessionKey());
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put("token",newToken);
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

    private class GetAPKLink extends AsyncTask<String,String,String>{

        SpotsDialog spotsDialog = new SpotsDialog(context,"Getting link of latest app");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spotsDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.GET_APK_LINK,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " ");

                            try {
                                JSONArray array = new JSONArray(response);
                                JSONObject object = array.getJSONObject(array.length()-1);
                                String link = object.getString("link");
                                Intent i = new Intent();
                                i.setAction(Intent.ACTION_SEND);
                                i.setType("text/plain");
                                i.putExtra(Intent.EXTRA_SUBJECT,"Try new Freelane App, bundle of employment oppertunities!!");
                                i.putExtra(Intent.EXTRA_TEXT,"Download and install Freelance App now\n\n"+link+"\nShared from Freelance");
                                startActivity(Intent.createChooser(i,"Share Freelance via..."));
                            } catch (JSONException e) {
                                e.printStackTrace();
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
