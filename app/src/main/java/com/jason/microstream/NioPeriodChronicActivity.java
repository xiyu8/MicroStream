package com.jason.microstream;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

@Deprecated
public class NioPeriodChronicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio_peroid_chronic);
        init();
    }

    EditText send_msg_content;
    EditText disconnect_user_name;
    EditText send_user_name;
    TextView receive_msg_content;
    TextView disconnect_client;
    TextView connected_users;
    NioPeriodChronicService.NioBinder nioBinder;
    protected void init() {
        send_msg_content = findViewById(R.id.send_msg_content);
        receive_msg_content = findViewById(R.id.receive_msg_content);
        disconnect_user_name = findViewById(R.id.disconnect_user_name);
        send_user_name = findViewById(R.id.send_user_name);
        disconnect_client = findViewById(R.id.disconnect_client);
        connected_users = findViewById(R.id.connected_users);
        disconnect_user_name.setText("user1");
        send_user_name.setText("user1");

        startService(new Intent(this, NioPeriodChronicService.class));

        bindService(new Intent(this, NioPeriodChronicService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                nioBinder = (NioPeriodChronicService.NioBinder) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                nioBinder.initNio(NioPeriodChronicActivity.this);

            }
        },5000);
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.disconnect_client:
                final String disconnectUserName = disconnect_user_name.getText().toString();
                if(disconnectUserName==null||disconnectUserName.equals("")) return;
                nioBinder.nioDisconnect();
                break;
            case R.id.send_msg:
                final String  sendMsg= send_msg_content.getText().toString();
                final String sendUser = send_user_name.getText().toString();
                if((sendMsg==null||sendMsg.equals(""))||(sendUser==null||sendUser.equals(""))) {
                    return;
                }
                nioBinder.nioWriteString(sendMsg);
                send_msg_content.setText("");
                break;
        }
    }


    public void showData(final String user, final String ss) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                receive_msg_content.append(user+"："+ss+"\n\r");
            }
        });

    }

    public void showError(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(NioPeriodChronicActivity.this, error, Toast.LENGTH_SHORT ).show();
            }
        });
    }



    public void showConnectedUsers(String userNames) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connected_users.setText(userNames);
            }
        });
    }


}