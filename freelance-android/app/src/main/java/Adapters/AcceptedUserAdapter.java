package Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.akash.applications.freelance.ProfileDetails;
import com.akash.applications.freelance.ProjectPushing.ChatRoom;
import com.akash.applications.freelance.ProjectPushing.ProjectAgreement;
import com.akash.applications.freelance.R;
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
import java.util.HashMap;
import java.util.Map;

import CustomJSONParser.ServerReply;
import DataModel.AcceptedUserModel;
import LocalPrefrences.SessionData;
import Utils.Constants;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by akash on 22/4/17.
 */

public class AcceptedUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<AcceptedUserModel> list;
    String projectName, projectID;

    public AcceptedUserAdapter(Context context, ArrayList<AcceptedUserModel> list, String projectName, String projectID) {
        this.context = context;
        this.list = list;
        this.projectName = projectName;
        this.projectID = projectID;
        Log.e("cheking","name --- "+projectName);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.accepeted_user_list_item, parent, false);
        return new AcceptedUserList(item);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final AcceptedUserList _holder = (AcceptedUserList) holder;
        final AcceptedUserModel model = list.get(position);

        _holder.name.setText(model.getName());
        _holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ProfileDetails.class)
                        .putExtra("id",model.getId())
                        .putExtra("image",Constants.USER_CURRENT_PROFILE_IMAGE+model.getImage())
                        .putExtra("name",model.getName()));

            }
        });

        if(model.getAcceptedState().equalsIgnoreCase("hired"))
        {
            _holder.hireBtn.setTextColor(context.getResources().getColor(R.color.colorAccent));
            _holder.hireBtn.setText("HIRED");
            _holder.hireBtn.setEnabled(false);
            _holder.hireBtn.setBackgroundResource(R.drawable.button_holo_bg);
            _holder.feedback.setVisibility(View.VISIBLE);
        }

        _holder.feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.feedback,null);
                builder.setView(dialogView);
                final SmileRating smileRating = (SmileRating) dialogView.findViewById(R.id.smile_rating);
                final EditText comment = (EditText) dialogView.findViewById(R.id.comment);
                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int rating = smileRating.getRating();
                        postRating(rating,comment.getText().toString(),model.getId());
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
        });

        Glide.with(context)
                .load(Constants.USER_CURRENT_PROFILE_IMAGE+model.getImage())
                .placeholder(R.drawable.userdp)
                .bitmapTransform(new CropCircleTransformation(context))
                .error(R.drawable.userdp)
                .into(_holder.img);
        _holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ProfileDetails.class)
                        .putExtra("id",model.getId()));
            }
        });

        _holder.hireBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,model.getId(),Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context, ProjectAgreement.class)
                .putExtra("workerID",model.getId())
                .putExtra("projectId",projectID)
                .putExtra("projectName",projectName)
                .putExtra("uImage",model.getImage())
                .putExtra("uName",model.getName())
                .putExtra("ratings","3.5")
                .putExtra("chatroomId",model.getChatroomId()));

            }
        });

        _holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatRoom.class)
                .putExtra("image",Constants.USER_CURRENT_PROFILE_IMAGE+model.getImage())
                .putExtra("name",model.getName())
                .putExtra("chatroomId",model.getChatroomId()));
            }
        });


    }

    private void postRating(final int rating, final String s, final String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.FEEDBACK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        Log.i("Checking", response + " ");
                        if(new ServerReply(response).getStatus())
                        {
                            Toast.makeText(context,"Comment and rating posted",Toast.LENGTH_LONG).show();
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
                params.put("content",s);
                params.put("type","user");
                params.put("fileId",id);
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

    private class AcceptedUserList extends RecyclerView.ViewHolder {
        TextView name, hireBtn;
        ImageView img, chat, feedback;
        MaterialRatingBar ratingBar;
        public AcceptedUserList(View item) {
            super(item);

            name = (TextView) item.findViewById(R.id.userListName);
            img = (ImageView) item.findViewById(R.id.userListImage);
            hireBtn = (TextView) item.findViewById(R.id.userListhire);
            chat = (ImageView) item.findViewById(R.id.chat);
            ratingBar = (MaterialRatingBar) item.findViewById(R.id.library_decimal_ratingbar);
            feedback = (ImageView) item.findViewById(R.id.feedback);
        }
    }
}
