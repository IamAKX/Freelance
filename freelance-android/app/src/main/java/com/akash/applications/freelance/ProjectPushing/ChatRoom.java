package com.akash.applications.freelance.ProjectPushing;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.akash.applications.freelance.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Adapters.ChatRoomAdapter;
import Connector.ConnectionDetector;
import DataModel.ChatRoomModel;
import LocalPrefrences.UserDetails;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
   
public class ChatRoom extends AppCompatActivity {
    String image, name, chatroomId;
    ListView chatList;
    DatabaseReference rootRoomName;
    Context context;
    EmojiconEditText message;
    ImageButton send;
    ImageView emojticon;
    ArrayList<ChatRoomModel> arrayList = new ArrayList<>();
    ChatRoomAdapter chatRoomAdapter;
    ConnectionDetector detector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = this;
        detector = new ConnectionDetector(context);
        image = getIntent().getStringExtra("image");
        name = getIntent().getStringExtra("name");
        chatroomId = getIntent().getStringExtra("chatroomId");
        chatList = (ListView) findViewById(R.id.chatList);
        message = (EmojiconEditText) findViewById(R.id.editTextmessage);
        send = (ImageButton) findViewById(R.id.sendButton);
        emojticon = (ImageView) findViewById(R.id.emojiBtn);
        getSupportActionBar().setTitle(name);

        if(!detector.isConnectingToInternet())
            detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");

        chatRoomAdapter = new ChatRoomAdapter(context,arrayList);
        chatList.setAdapter(chatRoomAdapter);

        View rootView = findViewById(R.id.activity_chat_room);
        EmojIconActions emojIconActions = new EmojIconActions(this, rootView, emojticon, message);
        emojIconActions.ShowEmojicon();
        
        rootRoomName = FirebaseDatabase.getInstance().getReference().getRoot().child("ChatRooms").child(chatroomId);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!detector.isConnectingToInternet())
                {
                    detector.showSnackBar(v,"You lack Internet Connectivity");
                    return;
                }

                String msg = message.getText().toString();
                if(!TextUtils.isEmpty(msg))
                {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM, h:mm a");
                    DatabaseReference childRoot = rootRoomName.push();
                    Map<String,Object> objectMap = new HashMap<String, Object>();
                    objectMap.put("name",new UserDetails(context).getUserName());
                    objectMap.put("message",msg);
                    objectMap.put("tstamp",simpleDateFormat.format(new Date()).toString());
                    childRoot.updateChildren(objectMap);
                    message.setText("");

                }
            }
        });

        rootRoomName.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                updateChatList(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                updateChatList(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void updateChatList(DataSnapshot dataSnapshot) {

        if(!detector.isConnectingToInternet())
        {
            detector.showSnackBar(getWindow().getDecorView(),"You lack Internet Connectivity");
            return;
        }
        String values = dataSnapshot.getValue(true).toString();
        values = values.substring(1,values.lastIndexOf('}'));
        String[] valueArray = values.split(",");
        Log.e("checking",valueArray[0]+" | "+valueArray[1]+" | "+valueArray[2]);
        String name = valueArray[0].substring(valueArray[0].indexOf('=')+1);
        String msg = valueArray[1].substring(valueArray[1].indexOf('=')+1);
        String tstamp = valueArray[2].substring(valueArray[2].indexOf('=')+1);
        boolean isMe = name.equalsIgnoreCase(new UserDetails(context).getUserName());

        ChatRoomModel model = new ChatRoomModel(msg,tstamp,isMe);
        chatRoomAdapter.add(model);
        chatRoomAdapter.notifyDataSetChanged();

    }

}
