package Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akash.applications.freelance.FullscreenImage;
import com.akash.applications.freelance.ProfileDetails;
import com.akash.applications.freelance.ProjectPushing.PostProject;
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
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import CustomJSONParser.ServerReply;
import DataModel.EmployeeModel;
import LocalPrefrences.SessionData;
import Utils.Constants;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by akash on 20/5/17.
 */

public class FavAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    ArrayList<EmployeeModel> list;
    Activity activity;
    public FavAdapter(Context context, Activity activity,ArrayList<EmployeeModel> list)
    {
        this.context = context;
        this.activity = activity;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View item = LayoutInflater.from(context).inflate(R.layout.home_menu_card, viewGroup, false);
        return new EmployeeViewHolder(item);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i)
    {
        final EmployeeViewHolder holder = (EmployeeViewHolder) viewHolder;
        final EmployeeModel model = list.get(i);
        holder.name.setText(model.getName());
        holder.category.setText(model.getCategories());

        holder.rating.setRating(Float.parseFloat(model.getRatings()));
        Glide.with(context)
                .load(Constants.USER_CURRENT_PROFILE_IMAGE+model.getImage())
                .placeholder(R.drawable.userdp)
                .bitmapTransform(new CropCircleTransformation(context))
                .error(R.drawable.userdp)
                .into(holder.profilePic);


        holder.likeButton.setLiked(model.isFav());
        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                addRemoveFav(model.getId());
                int pos = holder.getAdapterPosition();
                list.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos,list.size());
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                addRemoveFav(model.getId());
                int pos = holder.getAdapterPosition();
                list.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos,list.size());
            }
        });

        holder.profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context,FullscreenImage.class).putExtra("image",Constants.USER_CURRENT_PROFILE_IMAGE+model.getImage()).putExtra("name",model.getName()));
            }
        });

        holder.hireBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, PostProject.class).putExtra("wID",model.getId()).putExtra("wNAME",model.getName()));
            }
        });

        if(model.isCertified())
            holder.certified.setVisibility(View.VISIBLE);
        else
            holder.certified.setVisibility(View.GONE);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ProfileDetails.class)
                        .putExtra("image",Constants.USER_CURRENT_PROFILE_IMAGE+model.getImage())
                        .putExtra("name",model.getName())
                        .putExtra("id",model.getId())
                        .putExtra("phone",model.getPhone()));
            }
        });
    }

    private void addRemoveFav(final String id) {
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
                params.put("userId",id);
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

    private class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView name, category, hireBtn;
        ImageView profilePic, certified;
        LinearLayout linearLayout;
        LikeButton likeButton;
        MaterialRatingBar rating;
        public EmployeeViewHolder(View item) {
            super(item);
            name = (TextView) item.findViewById(R.id.home_card_name);
            category = (TextView) item.findViewById(R.id.home_card_category);
            rating = (MaterialRatingBar) item.findViewById(R.id.library_decimal_ratingbar);
            hireBtn = (TextView) item.findViewById(R.id.home_card_hire_btn);
            profilePic = (ImageView) item.findViewById(R.id.home_card_image);
            certified = (ImageView) item.findViewById(R.id.profFav);
            linearLayout = (LinearLayout) item.findViewById(R.id.cardVerLL);
            likeButton = (LikeButton) item.findViewById(R.id.home_card_favouriteBtn);
            category.setSelected(true);

        }
    }

}
