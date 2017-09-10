package Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akash.applications.freelance.FullscreenImage;
import com.akash.applications.freelance.ProjectPushing.UserSelection;
import com.akash.applications.freelance.R;
import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;
import java.util.zip.Inflater;

import DataModel.UserSelectionModel;
import Utils.Constants;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import lb.library.SearchablePinnedHeaderListViewAdapter;
import lb.library.StringArrayAlphabetIndexer;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by akash on 15/4/17.
 */

public class UserSelectionAdapter extends SearchablePinnedHeaderListViewAdapter<UserSelectionModel> {

    Context context;
    ArrayList<UserSelectionModel> arrayList = new ArrayList<>();
    LayoutInflater mInflater;

    public UserSelectionAdapter(Context context, ArrayList<UserSelectionModel> arrayList, LayoutInflater mInflater) {
        this.context = context;
        this.arrayList = arrayList;
        this.mInflater = mInflater;

        setSectionIndexer(new StringArrayAlphabetIndexer(generatedNames(),true));
    }

    private String[] generatedNames() {
        ArrayList<String> list = new ArrayList<>();
        if(arrayList != null)
            for (UserSelectionModel userSelectionModel : arrayList)
                list.add(userSelectionModel.getName());
        return list.toArray(new String[list.size()]);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final View rootView;

        if(convertView == null)
        {
            holder = new ViewHolder();
            rootView = mInflater.inflate(R.layout.user_selection_listview_items,parent,false);
            holder.img = (ImageView) rootView.findViewById(R.id.userListImage);
            holder.checkBox = (CheckBox) rootView.findViewById(R.id.userListCheckbox);
            holder.name = (TextView) rootView.findViewById(R.id.userListName);
            holder.headerName = (TextView) rootView.findViewById(R.id.header_text);
            rootView.setTag(holder);
        }
        else
        {
            rootView = convertView;
            holder = (ViewHolder) rootView.getTag();
        }
        final UserSelectionModel model = getItem(position);
        holder.name.setText(model.getName());
        Glide.with(context)
                .load(Constants.USER_CURRENT_PROFILE_IMAGE+model.getImage())
                .placeholder(R.drawable.userdp)
                .bitmapTransform(new CropCircleTransformation(context))
                .error(R.drawable.userdp)
                .into(holder.img);
        holder.checkBox.setChecked(model.isSelectedState());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    model.setSelectedState(isChecked);
                    if(isChecked)
                        UserSelection.selectUsersList.put(model.getId(),model.getName());
                    else
                        UserSelection.selectUsersList.remove(model.getId());
            }
        });
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetailedAlert(model);
            }
        });
        bindSectionHeader(holder.headerName,null,position);
        return rootView;
    }

    private void showDetailedAlert(final UserSelectionModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = mInflater.inflate(R.layout.user_selection_alertdialogbox_custom_layout,null);
        builder.setView(dialogView);
//                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
        ImageView image = (ImageView) dialogView.findViewById(R.id.alertImageView);
        TextView name = (TextView) dialogView.findViewById(R.id.alertName);
        TextView category = (TextView) dialogView.findViewById(R.id.alertCategory);
        MaterialRatingBar rating = (MaterialRatingBar) dialogView.findViewById(R.id.alertRatings);

        Glide.with(context)
                .load(Constants.USER_CURRENT_PROFILE_IMAGE+model.getImage())
                .placeholder(R.drawable.userdp)
                .error(R.drawable.userdp)
                .into(image);
        name.setText(model.getName());
        category.setText(model.getCategory());
        rating.setRating((float) 3.5);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, FullscreenImage.class).putExtra("name",model.getName()).putExtra("image",Constants.USER_CURRENT_PROFILE_IMAGE+model.getImage()));
            }
        });

        builder.create().show();

    }

    @Override
    public CharSequence getSectionTitle(int sectionIndex) {
        return ((StringArrayAlphabetIndexer.AlphaBetSection)getSections()[sectionIndex]).getName();
    }


    @Override
    public boolean doFilter(UserSelectionModel item, CharSequence constraint) {
        if(TextUtils.isEmpty(constraint))
            return true;
        final String displayName = item.getName();
        return !TextUtils.isEmpty(displayName) && displayName.toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault()));
    }

    @Override
    public ArrayList<UserSelectionModel> getOriginalList() {
        return arrayList;
    }

    private static class ViewHolder
    {
        ImageView img;
        TextView name, headerName;
        CheckBox checkBox;
    }
}
