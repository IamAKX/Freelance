package Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akash.applications.freelance.ProjectPushing.ChatRoom;
import com.akash.applications.freelance.R;
import com.akash.applications.freelance.ViewProject;
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
import com.hsalf.smilerating.SmileRating;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Connector.ConnectionDetector;
import CustomJSONParser.ServerReply;
import DataModel.OnGoingProjectModel;
import LocalPrefrences.SessionData;
import Utils.Constants;
import Utils.DateFormatManager;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by akash on 17/5/17.
 */

public class OnGoingProjectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<OnGoingProjectModel> list;
    Activity activity;
    ConnectionDetector detector;
    public OnGoingProjectAdapter(Context context, ArrayList<OnGoingProjectModel> list, Activity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
        detector = new ConnectionDetector(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.worker_ongoing_project, parent, false);
        return new AllOnGoingProjects(item);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final AllOnGoingProjects viewHolder = (AllOnGoingProjects) holder;
        final OnGoingProjectModel model = list.get(position);

        Glide.with(context)
                .load(Constants.USER_CURRENT_PROFILE_IMAGE+model.getUimage())
                .placeholder(R.drawable.userdp)
                .bitmapTransform(new CropCircleTransformation(context))
                .error(R.drawable.userdp)
                .into(viewHolder.userImage);

        viewHolder.name_postdate.setText(model.getUname().trim()+" posted on "+DateFormatManager.utcTODate(model.getPostedDate(),"dd MMM, yy"));
        viewHolder.deadline.setText("Deadline for this project is midnight of "+ DateFormatManager.utcTODate(model.getDeadline()," dd MMM, yy"));

        viewHolder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ViewProject.class).putExtra("id",model.getPid()));
            }
        });

        viewHolder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+model.getUphone()));
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(activity, new String[] { android.Manifest.permission.CALL_PHONE},Constants.PHONE_GROUP_CODE);
                else
                    context.startActivity(intent);
            }
        });

        viewHolder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatRoom.class)
                        .putExtra("image",Constants.USER_CURRENT_PROFILE_IMAGE+model.getUimage())
                        .putExtra("name",model.getUname())
                        .putExtra("chatroomId",model.getChatroomId()));
            }
        });

        viewHolder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close project
                showClosingAlert(viewHolder.getAdapterPosition(),model.getPid(),model.getUid());
                //closeProject(viewHolder.getAdapterPosition(),model.getPid());
            }
        });

        final Date future = DateFormatManager.dateFromUTC(model.getDeadline());
        final long dateDiffInSec = (future.getTime()-new Date().getTime())/1000;
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            long time = dateDiffInSec + (24*60*60);
            @Override
            public void run() {
                while (time > 0)
                {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            long interval = time;
                            long mnth = interval/(60*60*24*30);
                            interval %= (60*60*24*30);
                            long d = interval / (60*60*24);
                            interval %= (60*60*24);
                            long h = interval / (60*60);
                            interval %= (60*60);
                            long m = interval / 60;
                            interval %= (60);
                            viewHolder.months.setText(mnth+"\nmonth");
                            viewHolder.days.setText(d+"\nday");
                            viewHolder.hours.setText(h+"\nhour");
                            viewHolder.minutes.setText(m+"\nminute");
                            viewHolder.secs.setText(interval+"\nsec");
                            time--;
                        }
                    });

                    try {
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Log.e("checking","Time Difference : "+time);

                }
            }
        };

        new Thread(runnable).start();

    }

    private void showClosingAlert(final int adapterPosition, final String pid, final String uid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("You are about to close this Job, which means you have accomplished your alloted task. Do you want to close it?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showFeedback(adapterPosition,pid,uid);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showFeedback(final int adapterPosition, final String pid, final String uid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.feedback,null);
        builder.setView(dialogView);
        final SmileRating smileRating = (SmileRating) dialogView.findViewById(R.id.smile_rating);
        final EditText comment = (EditText) dialogView.findViewById(R.id.comment);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                int rating = smileRating.getRating();
                postRating(rating,comment.getText().toString(),adapterPosition,pid,uid);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    private void postRating(final int rating, final String comment, final int adaptedPos, final String pid, final String uid) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.FEEDBACK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        Log.i("Checking", response + " ");
                        if(new ServerReply(response).getStatus())
                        {
                            closeProject(adaptedPos,pid);
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
                params.put("rating", String.valueOf(rating));
                params.put("content",comment);
                params.put("type","user");
                params.put("fileId",uid);
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

    private void closeProject(final int adapterPosition, final String pid) {

        if(!new ConnectionDetector(context).isConnectingToInternet())
            return;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.PROJECT_DONE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        Log.i("Checking", response + " ");


                        list.remove(adapterPosition);
                        notifyItemRemoved(adapterPosition);
                        notifyItemRangeChanged(adapterPosition,list.size());


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
                // Adding parameters to request
                params.put("projectId",pid);
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
    public int getItemCount() {
        return list.size();
    }

    private class AllOnGoingProjects extends RecyclerView.ViewHolder {
        TextView months, days, hours, minutes, secs, name_postdate, deadline, details, chat, call, close;
        ImageView userImage;
        public AllOnGoingProjects(View item) {
            super(item);

            months = (TextView) item.findViewById(R.id.month);
            days = (TextView) item.findViewById(R.id.day);
            hours = (TextView) item.findViewById(R.id.hour);
            minutes = (TextView) item.findViewById(R.id.minute);
            secs = (TextView) item.findViewById(R.id.second);

            name_postdate = (TextView) item.findViewById(R.id.empName);
            deadline = (TextView) item.findViewById(R.id.deadline);
            details = (TextView) item.findViewById(R.id.details);
            chat = (TextView) item.findViewById(R.id.chat);
            call = (TextView) item.findViewById(R.id.call);
            close = (TextView) item.findViewById(R.id.end_project);
            userImage = (ImageView) item.findViewById(R.id.empImg);
        }
    }
}
