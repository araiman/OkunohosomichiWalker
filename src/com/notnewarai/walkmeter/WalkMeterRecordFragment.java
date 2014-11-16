package com.notnewarai.walkmeter;

import java.math.BigDecimal;

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

import android.util.Log;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import android.support.v4.app.Fragment;

public class WalkMeterRecordFragment extends Fragment implements OnClickListener{
	 private TextView stepsRecordTextView;
	 private TextView distanceRecordTextView2;
	 
	 WalkMeterService mWalkMeterService;

	 WalkMeterSQLiteOpenHelper db;
	 
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
	            screenDisplay();
	        }

	        @Override
	        public void onServiceDisconnected(ComponentName className) {
	            mWalkMeterService = null;
	        }
	    };
	    
	
	    
	    
	    
	 @Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    	
	    	View view =inflater.inflate(R.layout.walkmeter_record_fragment, container, false);
	    	
	    	//id変更せよ
	    	stepsRecordTextView = (TextView)view.findViewById(R.id.sum_steps_sensor);
	    	distanceRecordTextView2 = (TextView)view.findViewById(R.id.sum_distance_sensor);
	        
	    	return view;
	    }
	 
	 @Override
	    public void onAttach(Activity act){
	        super.onAttach(act);
	    }

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
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
	    
	    //ON OFF ないからいらない。
/*
	    public void startDisplay() {
	    		Log.d("startDisplay","started");
	    		
	        	if (mWalkMeterService.getState()) {
	            screenDisplay();
	        }
	    }
*/
	    public void screenDisplay() {
	    	Log.d("screenDisplay","started");
	    	//Log.d("sumAllSteps",""+sumAllSteps());
	    	//Log.d("calcAllDistance",""+calcAllDistance(sumAllSteps()));
	    	
	    	//計算式変更せよ
	    	stepsRecordTextView.setText(sumAllSteps() + getString(R.string.label_counter));
	        
	        //計算式変更せよ
	    	distanceRecordTextView2.setText(calcAllDistance(sumAllSteps()) + getString(R.string.label_counter2));
	        
	        
	        
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
	    
	    public int sumAllSteps(){
	    	Log.d("sumAllSteps","started");
	    	Log.d("sumAllSteps",""+mWalkMeterService.getCounter());
	    	Log.d("sumAllSteps",""+db.getAllHistorySteps());
	    	
	    	int sum_all_steps = mWalkMeterService.getCounter() + db.getAllHistorySteps(); 
	    	return sum_all_steps;
	    }
	   
	    //calcSumDistanceにする
	    public double calcAllDistance(int all_steps){
	    	    	
	    	
	    	double km_distance = all_steps * getHeight((MainActivity)getActivity()) * 0.45 / 100000;
	    	
	    	BigDecimal bd_km_distance = new BigDecimal(km_distance);
	    	
	    	BigDecimal down_bd_km_distance = bd_km_distance.setScale(1, BigDecimal.ROUND_DOWN);
	    	
	    	double all_distance = down_bd_km_distance.doubleValue();
	    	
	    	return all_distance;
	    }
	    
	    
	    //WalkMeterFragmentとの共通部品。同じところに入れたい
	    public int getHeight(Context context){
	    	SharedPreferences pref = context.getSharedPreferences( "name_and_height", Context.MODE_PRIVATE );
	    	
	    	String strHeight = pref.getString("user_height", "-1");
	    	
	    	return Integer.parseInt(strHeight);
	    }
	    
	    
	    
}