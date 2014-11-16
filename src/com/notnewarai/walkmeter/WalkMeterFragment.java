
package com.notnewarai.walkmeter;

import java.util.Date;
import java.text.SimpleDateFormat;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import android.support.v4.app.Fragment;



import java.math.BigDecimal;


public class WalkMeterFragment extends Fragment implements OnClickListener {
    private TextView mSensorTextView;
    private TextView mSensorTextView2;

    private Button mStartButton;
    private Button mStopButton;
    
    WalkMeterSQLiteOpenHelper db;
    
    private WalkMeterService mWalkMeterService;

    
 private class WalkMeterReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == WalkMeterService.ACTION) {
                screenDisplay();
            }
        }
    }


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	View view =inflater.inflate(R.layout.walkmeterfragment, container, false);
    	
    	mSensorTextView = (TextView)view.findViewById(R.id.text_sensor);
        mSensorTextView2 = (TextView)view.findViewById(R.id.text_sensor_distance);
        mStartButton = (Button)view.findViewById(R.id.button_start);
        mStopButton = (Button)view.findViewById(R.id.button_stop);
        mStartButton.setOnClickListener(this);
        mStopButton.setOnClickListener(this);
        
        
    	return view;
    }
    
    @Override
    public void onAttach(Activity act){
        super.onAttach(act);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        
        //↓自信ない
        
        db = new WalkMeterSQLiteOpenHelper((MainActivity)getActivity()); 
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
     
      
    }

    @Override
    public void onResume() {
        getActivity().startService(new Intent(getActivity(), WalkMeterService.class));
        getActivity().registerReceiver(mWalkMeterReceiver, new IntentFilter(WalkMeterService.ACTION));
        getActivity().bindService(new Intent(getActivity(), WalkMeterService.class), mServiceConnection,
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
        mSensorTextView2.setText(calcDistance(mWalkMeterService.getCounter()) + getString(R.string.label_counter2));
        
        /*計算がうまくいかないので、コメントアウト
        if(calcDistance(mWalkMeterService.getCounter()+db.getAllHistorySteps()) >= 2400 ){
        	
       
        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        	builder.setTitle("ゴール！！")
        		.setMessage("おめでとうー。")
        		.show();
        }
        */
        
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
        getActivity().unbindService(mServiceConnection); 
        getActivity().unregisterReceiver(mWalkMeterReceiver);
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
    	
    	
    	    	
    	
    	double km_distance = counter * getHeight((MainActivity)getActivity()) * 0.45 / 100000;
    	
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
    
}
