package com.qlf.messenger_client_demo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.PriorityQueue;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Messenger mService;

    private Button button;
    private ServiceConnection mConncetion = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            Message msg = Message.obtain(null,1);
            Bundle bundle = new Bundle();
            bundle.putString("msg","hello ,this is Client");
            msg.setData(bundle);

            msg.replyTo = mGetReplyMessenger;

            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setPackage("com.qlf.messenger_service_demo");
                intent.setAction("qlf");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startService(intent);
                bindService(intent,mConncetion,Context.BIND_AUTO_CREATE);
            }
        });

    }

    @Override
    protected void onDestroy() {
        unbindService(mConncetion);
        super.onDestroy();
    }

    private static class  MessengerHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 2:
                    Log.i(TAG, "===receive msg from Service :" + msg.getData().getString("reply"));
                    break;
                default:
                    super.handleMessage(msg);
                    break;
                }
            }

    }


    private Messenger mGetReplyMessenger = new Messenger(new MessengerHandler());
}
