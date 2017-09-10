package Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akash.applications.freelance.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import DataModel.CommentModel;
import Utils.Constants;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by akash on 22/5/17.
 */

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<CommentModel> list;

    public CommentAdapter(Context context, ArrayList<CommentModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new CommentList(item);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CommentList _holder = (CommentList) holder;
        CommentModel model = list.get(position);

        Glide.with(context)
                .load(Constants.USER_CURRENT_PROFILE_IMAGE+model.getImage())
                .placeholder(R.drawable.userdp)
                .error(R.drawable.userdp)
                .bitmapTransform(new CropCircleTransformation(context))
                .into(_holder.img);

        _holder.name.setText(model.getName());
        _holder.comment.setText(model.getComment());
        _holder.ratingBar.setRating(Float.parseFloat(model.getRating()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class CommentList extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name, comment;
        MaterialRatingBar ratingBar;
        public CommentList(View item) {
            super(item);

            img = (ImageView) item.findViewById(R.id.uimg);
            name = (TextView) item.findViewById(R.id.uname);
            comment = (TextView) item.findViewById(R.id.comment);
            ratingBar = (MaterialRatingBar) item.findViewById(R.id.library_decimal_ratingbar);
        }
    }
}
