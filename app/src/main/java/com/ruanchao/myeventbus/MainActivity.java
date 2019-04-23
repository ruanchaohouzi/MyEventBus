package com.ruanchao.myeventbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ruanchao.eventbus.Subscribe;
import com.ruanchao.eventbus.ThreadMode;
import com.ruanchao.eventbus.UserEventBus;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Test_" + MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UserEventBus.getDefault().register(this);
    }

    public void enterNextPage(View view) {
        startActivity(new Intent(this, SecondActivity.class));
    }

    @Subscribe(threadMode = ThreadMode.Async)
    public void showEvent(MsgEvent msgEvent){
        Log.i(TAG, msgEvent.getMsg());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserEventBus.getDefault().unRegister(this);
    }
}
