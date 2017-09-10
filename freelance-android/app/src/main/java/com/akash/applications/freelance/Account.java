package com.akash.applications.freelance;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import Utils.Constants;

public class Account extends AppCompatActivity {
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        context = this;
        getSupportActionBar().setTitle("Account");
        ListView listView = (ListView) findViewById(R.id.account_setting_listview);
        ArrayAdapter adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,android.R.id.text1, Constants.ACCOUNT_MENU_LIST);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 0:
                        startActivity(new Intent(context,ChangeNumber.class));
                        break;
                    case 1:
                        startActivity(new Intent(context,ChangeEmail.class));
                        break;
                    case 2:
                        startActivity(new Intent(context,ChangePassword.class));
                        break;
                    case 3:
                        startActivity(new Intent(context,DeleteAccount.class));
                        break;
                }
            }
        });
    }
}
