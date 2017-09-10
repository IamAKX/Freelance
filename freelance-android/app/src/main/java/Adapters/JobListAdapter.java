package Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akash.applications.freelance.R;
import com.akash.applications.freelance.ViewProject;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import DataModel.JobListModel;
import Utils.CategoryIcon;
import Utils.Constants;
import Utils.DateFormatManager;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by akash on 22/4/17.
 */

public class JobListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<JobListModel> list;

    public JobListAdapter(Context context, ArrayList<JobListModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.job_list_item, parent, false);
        return new JobList(item);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        JobList _holder = (JobList) holder;
        final JobListModel model = list.get(position);

        _holder.name.setText(model.getName().toUpperCase());
        _holder.status.setText(model.getProjectStatus().substring(0,1).toUpperCase()+model.getProjectStatus().substring(1));
        if(model.getProjectStatus().equalsIgnoreCase("done"))
            _holder.status.setTextColor(context.getResources().getColor(R.color.progress_good));
        else
            _holder.status.setTextColor(context.getResources().getColor(R.color.green));
        _holder.desc.setText(model.getDesc().substring(0,1).toUpperCase()+model.getDesc().substring(1));
        _holder.participants.setText("Participants count : "+model.getParticipant());
        _holder.createdDate.setText("Posted on : "+ DateFormatManager.utcTODate(model.getCreatAt(),"dd MMM, yy"));

        _holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ViewProject.class).putExtra("id",model.getId()));
            }
        });

        _holder.category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,model.getCategory(),Toast.LENGTH_LONG).show();
            }
        });
        Glide.with(context)
                .load(CategoryIcon.getIcon(model.getCategory()))
                .placeholder(R.drawable.info)
                .bitmapTransform(new CropCircleTransformation(context))
                .error(R.drawable.info)
                .into(_holder.category);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class JobList extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView name, status,desc,participants, createdDate;
        ImageView category;
        public JobList(View item) {
            super(item);

            cardView = (CardView) item.findViewById(R.id.mainCard);
            name = (TextView) item.findViewById(R.id.jobName);
            status = (TextView) item.findViewById(R.id.status);
            desc = (TextView) item.findViewById(R.id.details);
            participants = (TextView) item.findViewById(R.id.participants);
            createdDate = (TextView) item.findViewById(R.id.createdOn);
            category = (ImageView) item.findViewById(R.id.categoryIcon);
        }
    }
}
