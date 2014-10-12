
package com.notnewarai.walkmeter;

import android.app.Activity;
import android.app.AlertDialog;
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

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import java.math.BigDecimal;


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
    
    
    //Preferenceの値
    
    private SharedPreferences preference;
    
    private Editor editor;
    

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
        
        //プリファレンスの準備
        preference = getSharedPreferences("name_and_height", MODE_PRIVATE);
        editor = preference.edit();
        
      //初回起動だけの処理
        if(preference.getBoolean("Launched", false)==false){
        	Intent intent = new Intent();
        	intent.setClassName("com.notnewarai.walkmeter","com.notnewarai.walkmeter.FirstSetting");
        	startActivity(intent);
        	
        	//Launchedの書き換え（2回目以降は、設定しなくてもいいように）
        	editor.putBoolean("Launched", true);
        	editor.commit();
        	
        }
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
            screenDisplay(this);
        }
    }

    public void screenDisplay(Context context) {
        mSensorTextView.setText(mWalkMeterService.getCounter() + getString(R.string.label_counter));
        //身長を設定出来るようになり次第、getCounter()* 身長　* 0.45
        mSensorTextView2.setText(calcDistance(mWalkMeterService.getCounter()) + getString(R.string.label_counter2));
        if(calcDistance(mWalkMeterService.getCounter()+db.getAllHistorySteps()) >= 2400 ){
        	SharedPreferences pref = context.getSharedPreferences( "name_and_height", Context.MODE_PRIVATE );
       
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setTitle("ゴール！！")
        		.setMessage(String.format("おめでとう%sさん",pref.getString("user_name", "名無し")))
        		.show();
        }
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
    
    
    public double calcDistance(int counter){
    	
    	double km_distance = counter * getHeight(this) * 0.45 / 100000;
    	
    	BigDecimal bd_km_distance = new BigDecimal(km_distance);
    	
    	BigDecimal down_bd_km_distance = bd_km_distance.setScale(1, BigDecimal.ROUND_DOWN);
    	
    	double distance = down_bd_km_distance.doubleValue();
    	
    	return distance;
    }
    
    public int getHeight(Context context){
    	SharedPreferences pref = context.getSharedPreferences( "name_and_height", Context.MODE_PRIVATE );
    	
    	String strHeight = pref.getString("user_height", "-1");
    	
    	return Integer.parseInt(strHeight);
    }
    
    public double complete(int sum_counter){
    	double sum_distance = calcDistance(sum_counter);
    	return sum_distance;
    }
}
