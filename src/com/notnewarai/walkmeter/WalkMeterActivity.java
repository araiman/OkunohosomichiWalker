
package com.notnewarai.walkmeter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class WalkMeterActivity extends Activity implements OnClickListener {
    private TextView mSensorTextView;
    private TextView mSensorTextView2;

    private Button mStartButton;

    private Button mStopButton;
    
    WalkMeterSQLiteOpenHelper db;

 private class WalkMeterReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == WalkMeterService.ACTION) {
                screenDisplay();
            }
        }
    }

    private WalkMeterService mWalkMeterService;

    private final WalkMeterReceiver mWalkMeterReceiver = new WalkMeterReceiver();

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mWalkMeterService = ((WalkMeterService.WalkMeterBinder)service).getService();
            startDisplay();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mWalkMeterService = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mSensorTextView = (TextView)findViewById(R.id.text_sensor);
        mSensorTextView2 = (TextView)findViewById(R.id.text_sensor_distance);
        mStartButton = (Button)findViewById(R.id.button_start);
        mStopButton = (Button)findViewById(R.id.button_stop);
        mStartButton.setOnClickListener(this);
        mStopButton.setOnClickListener(this);
        
        db = new WalkMeterSQLiteOpenHelper(this);
    }

    @Override
    public void onResume() {
        startService(new Intent(this, WalkMeterService.class));
        registerReceiver(mWalkMeterReceiver, new IntentFilter(WalkMeterService.ACTION));
        bindService(new Intent(this, WalkMeterService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
        super.onResume();
    }

    public void startDisplay() {
        	if (mWalkMeterService.getState()) {
            screenDisplay();
        }
    }

    public void screenDisplay() {
        mSensorTextView.setText(mWalkMeterService.getCounter() + getString(R.string.label_counter));
        mSensorTextView2.setText(mWalkMeterService.getCounter()* 0.45 + getString(R.string.label_counter2)); 
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
        unbindService(mServiceConnection); 
        unregisterReceiver(mWalkMeterReceiver);
        if (!mWalkMeterService.getState())
            mWalkMeterService.stopSelf();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.button_start:
                if (mWalkMeterService.getState())
                    return;
                mWalkMeterService.startCount();
                break;
        case R.id.button_stop:
                if (!mWalkMeterService.getState())
                    return;
                mWalkMeterService.stopCount();
                break;
        }
    }
}
