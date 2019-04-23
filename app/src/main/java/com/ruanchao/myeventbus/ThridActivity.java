package com.ruanchao.myeventbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ruanchao.eventbus.UserEventBus;

public class ThridActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thrid);
    }

    public void sendMsg(View view) {

        MsgEvent msgEvent = new MsgEvent();
        msgEvent.setId(1);
        msgEvent.setMsg("msgEvent send a msg");

        UserEventBus.getDefault().post(msgEvent);

    }
}
