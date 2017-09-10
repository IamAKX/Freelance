package Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akash.applications.freelance.ProjectPushing.ChatRoom;
import com.akash.applications.freelance.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import DataModel.ChatListModel;
import Utils.Constants;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by akash on 9/5/17.
 */

public class ChatListAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Activity activity;
    Context context;
    ArrayList<ChatListModel> list;

    public ChatListAdapter(Activity activity, Context context, ArrayList<ChatListModel> list) {
        this.activity = activity;
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.chatlist_item, parent, false);
        return new AllChatRoom(item);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AllChatRoom viewHolder = (AllChatRoom) holder;
        final ChatListModel model = list.get(position);

        Glide.with(context)
                .load(Constants.USER_CURRENT_PROFILE_IMAGE+model.getImage())
                .placeholder(R.drawable.userdp)
                .error(R.drawable.userdp)
                .bitmapTransform(new CropCircleTransformation(context))
                .into(viewHolder.userDp);

        viewHolder.name.setText(model.getName());
        viewHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatRoom.class)
                        .putExtra("image",Constants.USER_CURRENT_PROFILE_IMAGE+model.getImage())
                        .putExtra("name",model.getName())
                        .putExtra("chatroomId",model.getChatroomId()));
            }
        });

        viewHolder.email.setText(model.getEmail());
        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatRoom.class)
                        .putExtra("image",Constants.USER_CURRENT_PROFILE_IMAGE+model.getImage())
                        .putExtra("name",model.getName())
                        .putExtra("chatroomId",model.getChatroomId()));
            }
        });

        viewHolder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+model.getPhone()));
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(activity, new String[] { android.Manifest.permission.CALL_PHONE},Constants.PHONE_GROUP_CODE);
                else
                    context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class AllChatRoom extends RecyclerView.ViewHolder {
        ImageButton call;
        ImageView userDp;
        TextView name, email;
        LinearLayout linearLayout;
        public AllChatRoom(View item) {
            super(item);
            call = (ImageButton) item.findViewById(R.id.callBtn);
            userDp = (ImageView) item.findViewById(R.id.userDPCL);
            name = (TextView) item.findViewById(R.id.userNameCL);
            email = (TextView) item.findViewById(R.id.userEmailCL);
            linearLayout = (LinearLayout) item.findViewById(R.id.llemp);
        }
    }
}
