package com.akash.applications.freelance;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.yalantis.ucrop.view.UCropView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import Connector.ConnectionDetector;
import CustomJSONParser.UpdateProfileImageParser;
import LocalPrefrences.SessionData;
import LocalPrefrences.UserDetails;
import Utils.Constants;
import Utils.ImageEncoder;
import dmax.dialog.SpotsDialog;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class CropImage extends AppCompatActivity {


    private Context context;
    private UCropView uCropView;
    Button buttonUpload;
    Bitmap croppedImage;
    String encodedImage;
    public static void startWithUri(@NonNull Context context, @NonNull Uri uri) {
        Intent intent = new Intent(context, CropImage.class);
        intent.setData(uri);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        getSupportActionBar().setTitle("Crop Image");
        context = this;
        final Uri croppedImageURI = getIntent().getData();
        try {
            croppedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), croppedImageURI);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("Checking",croppedImageURI+" ");

        buttonUpload = (Button) findViewById(R.id.upload_Image);
        try {
            uCropView = (UCropView) findViewById(R.id.ucrop);
            uCropView.getCropImageView().setImageUri(getIntent().getData(), null);
            uCropView.getOverlayView().setShowCropFrame(false);
            uCropView.getOverlayView().setShowCropGrid(false);
            uCropView.getOverlayView().setDimmedColor(Color.TRANSPARENT);
        } catch (Exception e) {
            Log.e("Checking", "setImageUri", e);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(getIntent().getData().getPath()).getAbsolutePath(), options);


        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encodedImage  = ImageEncoder.encode(croppedImage);
                new UploadProfileImage().execute();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.STORAGE_GROUP_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveCroppedImage();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void saveCroppedImage() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(CropImage.this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE },Constants.STORAGE_GROUP_CODE);

        } else {
            Uri imageUri = getIntent().getData();
            if (imageUri != null && imageUri.getScheme().equals("file")) {
                try {
                    copyFileToDownloads(getIntent().getData());
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Checking", imageUri.toString(), e);
                }
            } else {
                Toast.makeText(context, getString(R.string.toast_unexpected_error), Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void copyFileToDownloads(Uri croppedFileUri) throws Exception{
        String downloadsDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        String filename = String.format("%d_%s", Calendar.getInstance().getTimeInMillis(), croppedFileUri.getLastPathSegment());

        final File saveFile = new File(downloadsDirectoryPath, filename);

        FileInputStream inStream = new FileInputStream(new File(croppedFileUri.getPath()));
        FileOutputStream outStream = new FileOutputStream(saveFile);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();

        //Uploading cropped file


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cancel_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menu_item_cancel:
                startActivity(new Intent(context,ProfileSettings.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class UploadProfileImage extends AsyncTask<String,String,String>{

        SpotsDialog spotsDialog = new SpotsDialog(context,"Updating profile image...");
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spotsDialog.setCancelable(false);
            spotsDialog.show();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }

        @Override
        protected String doInBackground(String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.UPLOAD_PROFILE_IMAGE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            Log.i("Checking", response + " ");
                            if(new UpdateProfileImageParser(response).getStatus())
                            {   spotsDialog.dismiss();
                                new UserDetails(context).setUserImage(Constants.USER_CURRENT_PROFILE_IMAGE + new UpdateProfileImageParser(response).getImageName());

                                Glide.with(context)
                                        .load(new UserDetails(context).getUserImage())
                                        .bitmapTransform(new CropCircleTransformation(context))
                                        .placeholder(R.drawable.userdp)
                                        .error(R.drawable.userdp)
                                        .into(Home.userProfilePicture);

                                startActivity(new Intent(context, ProfileSettings.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
                            }
                            else
                            {
                                startActivity(new Intent(context, ProfileSettings.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                            Log.i("Checking","Fail to upload");
                            spotsDialog.dismiss();
                            NetworkResponse networkResponse = error.networkResponse;
                            Log.i("Checking","Fail to upload : "+networkResponse.statusCode);
                            if(networkResponse != null && networkResponse.data != null)
                            {
                                switch (networkResponse.statusCode)
                                {
                                    case 500:
                                        spotsDialog.dismiss();
                                        Toast.makeText(context,"Fail to update image",Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        spotsDialog.dismiss();
                                        Toast.makeText(context,"Fail to update image",Toast.LENGTH_SHORT).show();
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
                    Log.i("Checking",encodedImage);
                    params.put("imageString", encodedImage);
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
