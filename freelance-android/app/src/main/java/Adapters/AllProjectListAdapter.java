package Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akash.applications.freelance.ProfileDetails;
import com.akash.applications.freelance.ProjectPushing.PostedProjectDetails;
import com.akash.applications.freelance.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Random;

import DataModel.AllProjectListModel;
import DataModel.ParticipantForSmallIcons;
import Utils.CategoryIcon;
import Utils.Constants;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by akash on 20/4/17.
 */

public class AllProjectListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<AllProjectListModel> list;

    public AllProjectListAdapter(Context context, ArrayList<AllProjectListModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.all_project_list_item, parent, false);
        return new AllProject(item);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AllProject viewHolder = (AllProject) holder;
        final AllProjectListModel model = list.get(position);

        viewHolder.name.setText(model.getName());
//        viewHolder.desc.setText(model.getDecs());
//        viewHolder.desc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                launchProjectDetail(model);
//            }
//        });
        viewHolder.wholeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchProjectDetail(model);
            }
        });
        viewHolder.deadline.setText(Html.fromHtml("Deadline : <b>"+model.getDeadline()+"</b>"));
        viewHolder.status.setText(model.getStatus());
        ArrayList<ParticipantForSmallIcons> pList = model.getParticipantrs();
        for (final ParticipantForSmallIcons smallIcons : pList)
        {
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(40,40);
            layoutParams.setMargins(5,5,5,5);
            imageView.setLayoutParams(layoutParams);
            Glide.with(context)
                    .load(Constants.USER_CURRENT_PROFILE_IMAGE+smallIcons.getImage())
                    .placeholder(R.drawable.userdp)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .error(R.drawable.userdp)
                    .into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,smallIcons.getName(),Toast.LENGTH_SHORT).show();
                }
            });
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    context.startActivity(new Intent(context, ProfileDetails.class)
                            .putExtra("id",smallIcons.getId())
                            .putExtra("image",Constants.USER_CURRENT_PROFILE_IMAGE+smallIcons.getImage())
                            .putExtra("name",smallIcons.getName()));
                    return true;
                }
            });
            viewHolder.linearLayout.addView(imageView);
        }

        Glide.with(context)
                .load(CategoryIcon.getIcon(model.getCategory()))
                .placeholder(R.drawable.info)
                .bitmapTransform(new CropCircleTransformation(context))
                .error(R.drawable.info)
                .into(viewHolder.catIcon);
        viewHolder.catIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,model.getCategory(),Toast.LENGTH_SHORT).show();
            }
        });

        Log.i("checking",model.getCategory()+" "+CategoryIcon.getIcon(model.getCategory()));
    }

    private void launchProjectDetail(AllProjectListModel model) {
        context.startActivity(new Intent(context, PostedProjectDetails.class).putExtra("id",model.getId()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class AllProject extends RecyclerView.ViewHolder {
        TextView name,  deadline, status;
        ImageView catIcon;
        LinearLayout linearLayout, wholeLL;
        public AllProject(View item) {
            super(item);
            name = (TextView) item.findViewById(R.id.allProjectpname);
            //desc = (TextView) item.findViewById(R.id.allProjectpdesc);
            linearLayout = (LinearLayout) item.findViewById(R.id.participantsImageList);
            deadline = (TextView) item.findViewById(R.id.allProjectdeadline);
            status = (TextView) item.findViewById(R.id.allProjectstatus);
            catIcon = (ImageView) item.findViewById(R.id.catIcon);
            wholeLL = (LinearLayout) item.findViewById(R.id.ll);
        }
    }
}
