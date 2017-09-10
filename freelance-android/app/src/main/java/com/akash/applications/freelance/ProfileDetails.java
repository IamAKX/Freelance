package com.akash.applications.freelance;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.like.LikeButton;
import com.like.OnLikeListener;


import java.util.HashMap;
import java.util.Map;

import Connector.ConnectionDetector;
import CustomJSONParser.ProfileDetailsParser;
import CustomJSONParser.ServerReply;
import LocalPrefrences.SessionData;
import Utils.Constants;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class ProfileDetails extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;
    Context context;
    private LinearLayout mTitleContainer;
    private TextView mTitle;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private ImageView banner, abuse;
    private CircleImageView circleImageView;
    private TextView name, email, titleName,category,experience, highestQualification, qualification,address;
    String _id;
    ImageView dropDown, map,call;
    ProfileDetailsParser profileDetailsParser;
    private String phone = "";
    LikeButton likeButton;
    private String _image,_name;

    private void bindActivity() {

        context = this;
        _id = getIntent().getStringExtra("id");

        mToolbar        = (Toolbar) findViewById(R.id.main_toolbar);
        mTitle          = (TextView) findViewById(R.id.main_textview_title);
        mTitleContainer = (LinearLayout) findViewById(R.id.main_linearlayout_title);
        mAppBarLayout   = (AppBarLayout) findViewById(R.id.main_appbar);
        banner = (ImageView) findViewById(R.id.main_imageview_placeholder);
        circleImageView  = (CircleImageView) findViewById(R.id.circleUserProfilePicture);
        name = (TextView) findViewById(R.id.employeeName);
        titleName = (TextView) findViewById(R.id.main_textview_title);
        email = (TextView) findViewById(R.id.employeeEmail);
        category = (TextView) findViewById(R.id.profileDetails_category);
        experience = (TextView) findViewById(R.id.profileDetails_Experience);
        highestQualification = (TextView) findViewById(R.id.profileDetails_highestQualification);
        qualification = (TextView) findViewById(R.id.profileDetails_qualifications);
        address = (TextView) findViewById(R.id.profileDetails_Address);
        dropDown = (ImageView) findViewById(R.id.profileDetails_highestQualification_toggleIcon);
        map = (ImageView) findViewById(R.id.profileDetails_Address_mapicon);
        call = (ImageView) findViewById(R.id.content_profile_detailed_call);
        likeButton = (LikeButton) findViewById(R.id.home_card_favouriteBtn);
        abuse = (ImageView) findViewById(R.id.abuse);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        bindActivity();

        ConnectionDetector detector = new ConnectionDetector(context);
        if(detector.isConnectingToInternet())
            new FetchProfileDetails().execute();
        else
            detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connection");

        _image = getIntent().getStringExtra("image");
        _name = getIntent().getStringExtra("name");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        dropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(qualification.getVisibility() == View.GONE)
                {
                    qualification.setVisibility(View.VISIBLE);
                    dropDown.setImageResource(android.R.drawable.arrow_up_float);
                }
                else
                {
                    qualification.setVisibility(View.GONE);
                    dropDown.setImageResource(android.R.drawable.arrow_down_float);
                }


            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String adrs = address.getText().toString();
                if(adrs.equalsIgnoreCase("Address not available"))
                {
                    Toast.makeText(context,"No address to search",Toast.LENGTH_SHORT).show();
                    return;
                }
                adrs = adrs.substring(adrs.indexOf("\n")).trim();
                String uriAddress = "http://maps.google.co.in/maps?q="+adrs;
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uriAddress));
                context.startActivity(i);
            }
        });
        mAppBarLayout.addOnOffsetChangedListener(this);

        mToolbar.inflateMenu(R.menu.menu_full_profile);
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);

        name.setText(_name);
        titleName.setText(_name);
        Glide.with(this)
                .load(_image)
                .placeholder(R.drawable.userdp)
                .error(R.drawable.userdp)
                .into(banner);

        Glide.with(this)
                .load(_image)
                .placeholder(R.drawable.userdp)
                .error(R.drawable.userdp)
                .crossFade()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if(isFirstResource)
                            Glide.with(getBaseContext())
                            .load(_image)
                            .crossFade()
                            .into(circleImageView);

                        return false;
                    }
                })
                .into(circleImageView);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(context,ProfileImage.class).putExtra("image",_image).putExtra("name",_name));
                startActivity(new Intent(context,FullscreenImage.class).putExtra("image",_image).putExtra("name",_name));
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+phone));
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(ProfileDetails.this, new String[] { android.Manifest.permission.CALL_PHONE},Constants.PHONE_GROUP_CODE);
                else
                    context.startActivity(intent);
            }
        });

        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                addRemoveFav();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                addRemoveFav();
            }
        });

        abuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,ReportAbuse.class)
                .putExtra("type","user")
                .putExtra("field",_id));
            }
        });

        findViewById(R.id.comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,Comment.class)
                .putExtra("id",_id)
                .putExtra("name",_name)
                .putExtra("imgLink",_image)
                .putExtra("rating","3.5"));
            }
        });
    }



    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    private class FetchProfileDetails extends AsyncTask<String,String,String>{

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }

        @Override
        protected String doInBackground(String... params) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.PROFILE_DETAILS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("checking",response);


                            profileDetailsParser = new ProfileDetailsParser(response);

                            //category.setText(Html.fromHtml("<i>sad<font color=\"red\"> i am</font></i>"));
                            category.setText(profileDetailsParser.getCategory());
                            highestQualification.setText(Html.fromHtml(profileDetailsParser.getHighestQualification()));
                            qualification.setText(Html.fromHtml(profileDetailsParser.getQualification().replace("\n","<br />")));
                            //qualification.setText(profileDetailsParser.getQualification());
                            experience.setText(Html.fromHtml(profileDetailsParser.getExperience()));
                            experience.setMovementMethod(LinkMovementMethod.getInstance());
                            address.setText(Html.fromHtml(profileDetailsParser.getAddress()));
                            email.setText(profileDetailsParser.getEmail());
                            phone = profileDetailsParser.getPhone();
                            likeButton.setLiked(profileDetailsParser.isFav());
                            findViewById(R.id.mainLayout).setVisibility(View.VISIBLE);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want

                            NetworkResponse networkResponse = error.networkResponse;
                            Toast.makeText(context,new ServerReply(new String(error.networkResponse.data)).getReason(),Toast.LENGTH_LONG);
                            //Log.i("Checking", new String(error.networkResponse.data));
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
                    params.put("_id",_id);
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

    public void addRemoveFav()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.ADD_REMOVE_FAVOURITE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        Log.i("Checking", response + " ");
                        if(new ServerReply(response).getStatus())
                        {
                            Toast.makeText(context,new ServerReply(response).getReason(),Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want

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
                params.put("userId",_id);
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
}