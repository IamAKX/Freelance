package com.akash.applications.freelance;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.akash.applications.freelance.ProfileSettingsOptions.Address;
import com.akash.applications.freelance.ProfileSettingsOptions.EducationDetails;
import com.akash.applications.freelance.ProfileSettingsOptions.Experience;
import com.akash.applications.freelance.ProfileSettingsOptions.Occupation;
import com.akash.applications.freelance.ProfileSettingsOptions.SelectAppMode;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import Connector.ConnectionDetector;
import CustomJSONParser.ServerReply;
import LocalPrefrences.SessionData;
import LocalPrefrences.UserDetails;
import Utils.Constants;
import dmax.dialog.SpotsDialog;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static java.lang.Thread.sleep;

public class ProfileSettings extends AppCompatActivity {
    Context context;
    ArcProgress arcProgress;
    MaterialEditText profileUserName;
    private TextView profileEmail;
    private AlertDialog dpDialogBox;
    private Uri takenImage = null;

    ConnectionDetector detector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getSupportActionBar().setTitle("Profile Settings");
        context = this;

        final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        profileUserName = (MaterialEditText) findViewById(R.id.profile_username);
        profileUserName.setText(new UserDetails(context).getUserName());
        profileUserName.setTag(profileUserName.getKeyListener());
        profileUserName.setKeyListener(null);

        profileEmail = (TextView) findViewById(R.id.profile_email);
        profileEmail.setText(new UserDetails(context).getUserEmail());
        profileEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,SetNewEmail.class));
            }
        });

        final ListView listView = (ListView) findViewById(R.id.profile_listview);
        arcProgress = (ArcProgress) findViewById(R.id.arc_progress);

        ArrayAdapter adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,android.R.id.text1, Constants.PROFILE_MENU_LIST);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 0:
                        startActivity(new Intent(context, SelectAppMode.class));
                        break;
                    case 1:
                        startActivity(new Intent(context, SelectCategory.class));
                        break;
                    case 2:
                        startActivity(new Intent(context, Address.class));
                        break;
                    case 3:
                        startActivity(new Intent(context, EducationDetails.class));
                        break;
                    case 4:
                        startActivity(new Intent(context, Occupation.class));
                        break;
                    case 5:
                        startActivity(new Intent(context, Experience.class));
                        break;
                }
            }
        });


        final ImageView profileImage = (ImageView) findViewById(R.id.profileImageView);
        Glide.with(context)
                .load(new UserDetails(context).getUserImage())
                //.bitmapTransform(new RoundedCornersTransformation(context,7,0))
                .bitmapTransform(new CropCircleTransformation(context))
                .placeholder(R.drawable.userdp)
                .error(R.drawable.userdp)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
                .into(profileImage);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pickFromGallery();
                showChangeProfilePictureDialog();
            }
        });


        profileUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileUserName.setKeyListener((KeyListener)profileUserName.getTag());
                profileUserName.setHelperText("This name will be visible to other.");
                profileUserName.setSelection(profileUserName.getText().toString().length());
                profileUserName.setHideUnderline(false);
                inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
            }
        });

        profileUserName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    if(!detector.isConnectingToInternet())
                    {
                        detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");
                        return true;
                    }
                    profileUserName.setHelperText(null);
                    profileUserName.clearFocus();
                    profileUserName.setHideUnderline(true);
                    profileUserName.setTag(profileUserName.getKeyListener());
                    profileUserName.setKeyListener(null);
                    new ChangeProfileName().execute();
                }
                return false;
            }
        });


        getProfilePercent();
        //updateProgress(88);
    }

    private void getProfilePercent() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.GET_PROFILE_PERCENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        Log.i("Checking", response + " ");
                        try {
                            JSONObject object = new JSONObject(response);
                            int percent = object.getInt("percent");
                            new UserDetails(context).setProfilePercent(String.valueOf(percent));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        updateProgress(new UserDetails(context).getProfilePercent());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want

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

    }

    private void showChangeProfilePictureDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.update_profile_pic_layout, null);

        ImageView camerabtn = (ImageView)alertLayout.findViewById(R.id.camera_btn);
        ImageView gallerybtn = (ImageView)alertLayout.findViewById(R.id.gallery_btn);

        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ProfileSettings.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(ProfileSettings.this, new String[] { android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE },Constants.CAMERA_GROUP_CODE);
                else
                    pickFromCamera();

                dpDialogBox.dismiss();
            }
        });

        gallerybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ProfileSettings.this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    pickFromGallery();
                else
                    ActivityCompat.requestPermissions(ProfileSettings.this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE },Constants.STORAGE_GROUP_CODE);
                dpDialogBox.dismiss();
            }
        });
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert
                .setView(alertLayout)
                .setCancelable(true);



        dpDialogBox = alert.create();
        dpDialogBox.show();
    }




    private void updateProgress(final int percent) {
        arcProgress.setProgress(0);
        Log.i("checking","percent = "+percent);
        final Handler handler = new Handler();
        Runnable runable = new Runnable() {
            int currentProgress = 0;
            @Override
            public void run() {
                while (currentProgress < percent){
                currentProgress++;
                    try {
                        sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                    @Override
                    public void run() {
                        arcProgress.setProgress(currentProgress);
                        if(currentProgress <=10)
                            arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.adctive_red));
                        else if(currentProgress >10 && currentProgress <=24)
                            arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.progress_fair));
                        else if(currentProgress > 24 && currentProgress <= 75)
                            arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.progress_good));
                        else if(currentProgress > 75)
                            arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.progress_best));
                    }
                });

            }
            }
        };
        new Thread(runable).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case Constants.STORAGE_GROUP_CODE:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED))
                {
                    pickFromGallery();
                }
                else {
                    Toast.makeText(context,"Access to Storage is denied. You cannot select image",Toast.LENGTH_LONG);
                }
                break;
            case Constants.CAMERA_GROUP_CODE:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED))
                {
                    pickFromCamera();
                }
                else
                {
                    Toast.makeText(context,"Access to Camera is denied. You cannot take image",Toast.LENGTH_LONG);
                }
                break;
        }
    }

    //  Launching camera
    private void pickFromCamera() {
        try{
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            String imgDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Freelance/Freelance Camera";
            File f = new File(imgDirectory);
            if(!f.exists())
                f.mkdirs();
            File imageFile = new File(imgDirectory+"/IMG_"+System.currentTimeMillis()+".jpg");
            takenImage = Uri.fromFile(imageFile);
            takePictureIntent.putExtra( android.provider.MediaStore.EXTRA_OUTPUT, takenImage);
            startActivityForResult(takePictureIntent, Constants.PICK_FROM_CAMERA);

        }

        catch (ActivityNotFoundException err)
        {
            Toast.makeText(getBaseContext(),"Camera Not available or Give the permission of the camera from the Settings manually", Toast.LENGTH_LONG).show();
        }
    }

    //  Opening gallery
    private void pickFromGallery() {

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            try {
                intent.putExtra("return-data",true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Choose image using"),Constants.PICK_FROM_GALLERY);
            }
            catch (ActivityNotFoundException e)
            {
                Log.e("Checking",e.getMessage());
            }
    }

    private void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = "IMG"+ Calendar.getInstance().getTimeInMillis()+".jpg";


        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));

        uCrop = advancedConfig(uCrop);

        uCrop.start(ProfileSettings.this);
    }

    private UCrop advancedConfig(@NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();

        options.setCompressionQuality(100);
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);
        return uCrop.withOptions(options);
    }
    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            CropImage.startWithUri(ProfileSettings.this, resultUri);
        } else {
            Toast.makeText(context, "Cannot parse the image", Toast.LENGTH_SHORT).show();
        }
    }
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e("Checking", "handleCropError: ", cropError);
            Toast.makeText(ProfileSettings.this, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(ProfileSettings.this, R.string.toast_unexpected_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == Constants.PICK_FROM_GALLERY)
            {

                final Uri selectedUri = data.getData();

                if (selectedUri != null)
                {
                    startCropActivity(data.getData());
                }
                else {
                    Toast.makeText(context, "Cannot parse the image", Toast.LENGTH_SHORT).show();
                }
            }
            else
                if(requestCode == Constants.PICK_FROM_CAMERA)
                {
                    if (takenImage != null)
                    {
                        startCropActivity(takenImage);
                    }
                    else {
                        Toast.makeText(context, "Cannot parse the image", Toast.LENGTH_SHORT).show();
                    }
                }
            else
            if (requestCode == UCrop.REQUEST_CROP)
            {
                handleCropResult(data);
            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError(data);
        }
    }

    private class ChangeProfileName extends AsyncTask<String,String ,String>{

        SpotsDialog dialog = new SpotsDialog(context,"Updating your name...");
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}


