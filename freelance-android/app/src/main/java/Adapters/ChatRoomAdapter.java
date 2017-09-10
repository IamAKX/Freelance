package Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akash.applications.freelance.R;

import java.util.ArrayList;

import DataModel.ChatRoomModel;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

/**
 * Created by akash on 9/5/17.
 */

public class ChatRoomAdapter extends BaseAdapter{
    Context context;
    ArrayList<ChatRoomModel> arrayList = new ArrayList<>();
    static LayoutInflater layoutInflater = null;
    public ChatRoomAdapter(Context context, ArrayList<ChatRoomModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatRoomModel model = arrayList.get(position);
        View vi = convertView;
        if(convertView == null)
            vi = layoutInflater.inflate(R.layout.chat_bubble,null);
        EmojiconTextView msg = (EmojiconTextView) vi.findViewById(R.id.message_text);
        msg.setText(model.getMsg());
        LinearLayout layout = (LinearLayout) vi
                .findViewById(R.id.bubble_layout);
        LinearLayout parent_layout = (LinearLayout) vi
                .findViewById(R.id.bubble_layout_parent);

        // if message is mine then align to right
        if (model.isMe()) {
            layout.setBackgroundResource(R.drawable.bubbleright);
            parent_layout.setGravity(Gravity.RIGHT);
        }
        // If not mine then align to left
        else {
            layout.setBackgroundResource(R.drawable.bubbleleft);
            parent_layout.setGravity(Gravity.LEFT);
        }
        msg.setTextColor(Color.BLACK);
        return vi;
    }

    public void add(ChatRoomModel model)
    {
        arrayList.add(model);
    }
}
