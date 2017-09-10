package com.akash.applications.freelance.ProfileSettingsOptions;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.akash.applications.freelance.ProfileSettings;
import com.akash.applications.freelance.R;
import com.android.volley.AuthFailureError;
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
import com.eyalbira.loadingdots.LoadingDots;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Connector.ConnectionDetector;
import CustomJSONParser.ExperienceDetailsParser;
import CustomJSONParser.ServerReply;
import LocalPrefrences.SessionData;
import Utils.Constants;
import cn.refactor.kmpautotextview.KMPAutoComplTextView;
import dmax.dialog.SpotsDialog;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class Experience extends AppCompatActivity {

    private static final String SAVE_MODE = "save";
    Context context;

//    List<String> data;
    LinearLayout experiencePrompt, experienceContainer;
    boolean isEditable = true;
    LoadingDots loadingDots;
    ConnectionDetector detector;
    private Activity activity;

    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience);

        getSupportActionBar().setTitle("Experience");
        context = this;
        activity = this;
        detector = new ConnectionDetector(context);
//        data = new ArrayList<String>();
//        data.add("Driver");
//        data.add("Tailor");
//        data.add("Carpenter");
//        data.add("Tutor");
//        data.add("Caterer");
//        data.add("Computer Engineer");
//        data.add("Plumber");
//        data.add("Electrician");
//        data.add("Electrical Engineer");
//        data.add("Care Taker");
//        data.add("Grocery Supplier");
//        data.add("Maid Servant");
//        data.add("House keeping");
//        data.add("Mechanic");
//        data.add("Computer Servicing");
//        data.add("Litrature Tutor");
//        data.add("Broker");
//        data.add("House rent");

        loadingDots = (LoadingDots) findViewById(R.id.getExperienceloadingDotProgress);
        experiencePrompt = (LinearLayout) findViewById(R.id.experience_prompt);
        experienceContainer = (LinearLayout) findViewById(R.id.experienceLinearContainer);

        experiencePrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExperienceCard(SAVE_MODE, "nocategory", null, null, new String[0], "noid");
            }
        });
        experienceContainer.setLayoutTransition(new LayoutTransition());
        if (detector.isConnectingToInternet())
            new LoadExperience().execute();
        else
            detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");
    }

    private void showExperienceCard(String cardCommandMode, String category, final String desc, String linkUrl, String[] imgUrl, final String cardID) {
        experienceContainer.setVisibility(View.VISIBLE);

        if (experiencePrompt.getVisibility() == View.VISIBLE)
            experiencePrompt.setVisibility(View.GONE);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addView = layoutInflater.inflate(R.layout.add_experience_card, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,20);
        final RelativeLayout.LayoutParams imageRelativeLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        imageRelativeLayout.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        imageRelativeLayout.setMargins(20,20,20,20);

        //Initilize Componets
        final Button addButton;
        final ImageView delete, save;
        final MaterialEditText description, link, imgLink;
        final LinearLayout linearLayout;
        final KMPAutoComplTextView complTextView;
        final List<String> imagesList = new ArrayList<String>();

        complTextView = (KMPAutoComplTextView) addView.findViewById(R.id.experience_category);
        complTextView.setDatas(Constants.categoryList());
        description = (MaterialEditText) addView.findViewById(R.id.experienceDetails);
        link = (MaterialEditText) addView.findViewById(R.id.experienceLink);
        delete = (ImageView) addView.findViewById(R.id.delete_experience);
        save = (ImageView) addView.findViewById(R.id.experience_cardUpdate);
        linearLayout = (LinearLayout) addView.findViewById(R.id.experienceHorizontalLineraLayout);
        imgLink = (MaterialEditText) addView.findViewById(R.id.experienceImagelink);
        addButton = (Button) addView.findViewById(R.id.experienceImagelinkAddButton);

        int dimens = 45;
        float density = getResources().getDisplayMetrics().density;
        dimens = (int) (dimens * density);
        final LinearLayout.LayoutParams dimension = new LinearLayout.LayoutParams(dimens,dimens);

        //Set components with corresponding values
        if (!category.equals("nocategory"))
            complTextView.setText(category);

        description.setText(desc);
        link.setText(linkUrl);


        complTextView.dismissDropDown();

        if(!(imgUrl ==null) && imgUrl.length > 0)
        {
            for(final String img : imgUrl)
            {
                imagesList.add(img);
                Log.e("Checking",img);
                final RelativeLayout layout = new RelativeLayout(context);
                ImageView deleteImage = new ImageView(context);
                ImageView imageView = new ImageView(context);
                layout.setGravity(Gravity.CENTER);
                layout.setLayoutParams(imageRelativeLayout);
                imageView.setLayoutParams(dimension);
                deleteImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.delete_view));
                deleteImage.setVisibility(View.GONE);
                layout.addView(imageView);
                layout.addView(deleteImage);
                Glide.with(context)
                        .load(img)
                        .bitmapTransform(new RoundedCornersTransformation(context,5,0))
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_gallery)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                layout.setTag(img);
                                linearLayout.addView(layout);
                                return false;
                            }
                        })
                        .into(imageView);



            }
        }


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!detector.isConnectingToInternet())
                {
                    detector.showSnackBar(v,"You lack Internet Connectivity");
                    return;
                }
                ImageView imageView = new ImageView(context);
                imageView.setLayoutParams(dimension);
                final RelativeLayout layout = new RelativeLayout(context);
                ImageView deleteImage = new ImageView(context);
                layout.setGravity(Gravity.CENTER);
                layout.setLayoutParams(imageRelativeLayout);
                imageView.setLayoutParams(dimension);
                deleteImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.delete_view));
                deleteImage.setVisibility(View.GONE);
                layout.addView(imageView);
                layout.addView(deleteImage);
                Glide.with(context)
                        .load(imgLink.getText().toString())
                        .bitmapTransform(new RoundedCornersTransformation(context,5,0))
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_delete)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                Toast.makeText(context,"Failed to fetch image",Toast.LENGTH_SHORT).show();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                imagesList.add(imgLink.getText().toString());
                                layout.setTag(imgLink.getText().toString());
                                imgLink.setText("");
                                linearLayout.addView(layout);
                                return false;
                            }
                        })
                        .into(imageView);


            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LinearLayout) addView.getParent()).removeView(addView);
                if (experienceContainer.getChildCount() < 1) {
                    experiencePrompt.setVisibility(View.VISIBLE);
                    experienceContainer.setVisibility(View.GONE);
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make save edit mechanism
                if(!detector.isConnectingToInternet())
                {
                    detector.showSnackBar(v,"You lack Internet Connectivity");
                    return;
                }
                if (isEditable) {
                    //Call save card api
                    if (complTextView.getText().toString().equals("")) {
                        complTextView.setError("Select category");
                    } else if (description.getText().toString().equals("")) {
                        description.setError("Description is required");
                    } else {
                        if (cardID.equals("noid"))
                            new InsertExperience(complTextView.getText().toString(), description.getText().toString(), link.getText().toString(), getImageArray(imagesList)).execute();
                        else
                            new UpdateExperience(cardID, complTextView.getText().toString(), description.getText().toString(), link.getText().toString(), getImageArray(imagesList)).execute();
                        complTextView.setEnabled(false);
                        complTextView.setSelection(complTextView.getText().toString().length());
                        link.setEnabled(false);
                        link.setSelection(link.getText().toString().length());
                        description.setEnabled(false);
                        addButton.setEnabled(false);
                        imgLink.setEnabled(false);
                        description.setSelection(description.getText().toString().length());
                        save.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.edit));
                        for(int i = 0;i<linearLayout.getChildCount();i++)
                        {
                            RelativeLayout layout = (RelativeLayout) linearLayout.getChildAt(i);
                            layout.getChildAt(0).setAlpha((float)1 );
                            layout.getChildAt(1).setVisibility(View.GONE);
                        }
                        isEditable = false;
                    }
                } else {
                    complTextView.setEnabled(true);
                    description.setEnabled(true);
                    link.setEnabled(true);
                    addButton.setEnabled(true);
                    imgLink.setEnabled(true);
                    save.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.save));
                    for(i = 0;i<linearLayout.getChildCount();i++)
                    {
                        final RelativeLayout layout = (RelativeLayout) linearLayout.getChildAt(i);
                        layout.getChildAt(0).setAlpha((float) 0.5);
                        layout.getChildAt(1).setAlpha((float) 0.8);
                        layout.getChildAt(1).setVisibility(View.VISIBLE);

                        layout.getChildAt(1).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                imagesList.remove(layout.getTag());
                                linearLayout.removeView(linearLayout.getChildAt(linearLayout.indexOfChild(layout)));

                            }
                        });
                    }
                    isEditable = true;


                }
            }
        });

        if (cardCommandMode.equalsIgnoreCase(SAVE_MODE)) {
            complTextView.setEnabled(true);
            complTextView.setSelection(complTextView.getText().toString().length());
            link.setEnabled(true);
            addButton.setEnabled(true);
            imgLink.setEnabled(true);
            link.setSelection(link.getText().toString().length());
            description.setEnabled(true);
            description.setSelection(description.getText().toString().length());
            save.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.save));

            isEditable = true;
        } else {

            complTextView.setEnabled(false);
            description.setEnabled(false);
            link.setEnabled(false);
            addButton.setEnabled(false);
            imgLink.setEnabled(false);
            save.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.edit));
            isEditable = false;
        }

        addView.setLayoutParams(layoutParams);
        experienceContainer.addView(addView, 0);
    }

    private String getImageArray(List<String> imagesList) {

        JSONArray array = new JSONArray();


        String s="[";
        if(imagesList.size()==0)
            return "[]";
        for(int i = 0;i< imagesList.size();i++)
        {
            array.put(imagesList.get(i));
            if(i<imagesList.size()-1)
                s+="\""+imagesList.get(i)+"\",";
            else
                s+="\""+imagesList.get(i)+"\"]";
        }

        return s;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_done:
                //Save Setting
                startActivity(new Intent(context, ProfileSettings.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
                return true;
            case R.id.menu_item_add:
                showExperienceCard(SAVE_MODE, "nocategory", null, null, new String[0], "noid");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class UpdateExperience extends AsyncTask<String, String, String> {
        SpotsDialog spotsDialog = new SpotsDialog(context, "Saving experience");
        String cardId, cat, des, projLink, image;

        public UpdateExperience(String cardId, String cat, String des, String projLink, String image) {
            this.cardId = cardId;
            this.cat = cat;
            this.des = des;
            this.projLink = projLink;
            this.image = image;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spotsDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.UPDATE_EXPERIENCE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            if(new ServerReply(response).getStatus())
                                Toast.makeText(context,"Details update successfully.",Toast.LENGTH_SHORT).show();
                            spotsDialog.dismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                            Toast.makeText(context,"Failed to update.",Toast.LENGTH_SHORT).show();
                            spotsDialog.dismiss();


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

                    params.put("expId",cardId);
                    params.put("category",cat);
                    params.put("details",des);
                    params.put("link",projLink);
                    params.put("image",image);

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

    private class InsertExperience extends AsyncTask<String, String, String> {
        SpotsDialog spotsDialog = new SpotsDialog(context, "Saving experience");
        String cat, des, projLink, image;

        public InsertExperience(String cat, String des, String projLink, String image) {
            this.cat = cat;
            this.des = des;
            this.projLink = projLink;
            this.image = image;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spotsDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.INSERT_EXPERIENCE,
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
                    params.put("category",cat);
                    params.put("details",des);
                    params.put("link",projLink);
                    params.put("image",image);

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

    private class LoadExperience extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDots.startAnimation();
            loadingDots.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.FETCH_EXPERIENCE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            loadingDots.setVisibility(View.GONE);
                            try {
                                Log.i("Checking",response);
                                JSONArray array = ExperienceDetailsParser.getExperienceArray(response);

                                if(array.length()>0)
                                {
                                    for(int i = 0; i < array.length(); i++)
                                    {
                                        String experienceSet = array.getJSONObject(i).toString();
                                        ExperienceDetailsParser parser = new ExperienceDetailsParser(experienceSet);
                                        showExperienceCard("edit",parser.getCategory(),parser.getDetails(),parser.getLink(),parser.getImage(),parser.getId());
                                    }
                                }
                                else
                                {
                                    experiencePrompt.setVisibility(View.VISIBLE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                            Toast.makeText(context,"Failed to load data.",Toast.LENGTH_SHORT).show();
                            loadingDots.stopAnimation();
                            loadingDots.setVisibility(View.GONE);
                            experiencePrompt.setVisibility(View.VISIBLE);

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