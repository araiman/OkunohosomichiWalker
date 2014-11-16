package com.notnewarai.walkmeter;

import java.math.BigDecimal;


import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.IBinder;

import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;


public class WalkMeterWhereFragment extends Fragment implements OnClickListener {
	
	private ImageView whereImageView;
	private TextView whereTextView;
	private TextView haikuTextView;
	private TextView nowTextView;

	
	WalkMeterService mWalkMeterService;

	WalkMeterSQLiteOpenHelper db;
	
	private class WalkMeterReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == WalkMeterService.ACTION) {
            }
        }
    }
	
	
	
	
    private final WalkMeterReceiver mWalkMeterReceiver = new WalkMeterReceiver();

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mWalkMeterService = ((WalkMeterService.WalkMeterBinder)service).getService();
        	whereDisplay(); 
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mWalkMeterService = null;
        }
    };	
		
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	View view =inflater.inflate(R.layout.walkmeter_where, container, false);
    
    	whereImageView = (ImageView)view.findViewById(R.id.dummyImageView);
    	haikuTextView = (TextView)view.findViewById(R.id.dummyTextView2);
    	nowTextView = (TextView)view.findViewById(R.id.dummyTextView);
    	
    	
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



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!mWalkMeterService.getState())
            mWalkMeterService.stopSelf();

    }
   
    
    @Override
    public void onClick(View V)
    {

    } 
    
    		
    public int sumAllSteps(){
    	Log.d("sumAllSteps","started");
    	Log.d("sumAllSteps",""+mWalkMeterService.getCounter());
    	Log.d("sumAllSteps",""+db.getAllHistorySteps());
    	
    	int sum_all_steps = mWalkMeterService.getCounter()+db.getAllHistorySteps(); 
    	return sum_all_steps;
    }
    
    
    public int getHeight(Context context){
    	SharedPreferences pref = context.getSharedPreferences( "name_and_height", Context.MODE_PRIVATE );
    	
    	String strHeight = pref.getString("user_height", "-1");
    	
    	return Integer.parseInt(strHeight);
    }
    
    
    public double calcAllDistance(int all_steps){
    	
    	
    	double km_distance = all_steps * getHeight((MainActivity)getActivity()) * 0.45 / 100000;
    	
    	BigDecimal bd_km_distance = new BigDecimal(km_distance);
    	
    	BigDecimal down_bd_km_distance = bd_km_distance.setScale(1, BigDecimal.ROUND_DOWN);
    	
    	double all_distance = down_bd_km_distance.doubleValue();
    	
    	return all_distance;
    }
    
    public void whereDisplay(){
    	Log.d("whereDisplay","started");
    	int all_steps = sumAllSteps();
    	double allDistance = calcAllDistance(all_steps);
    	
    	if(0<=allDistance && allDistance<34){
    		//東京
    		whereImageView.setImageResource(R.drawable.tokyo);
    		nowTextView.setText("東京なう");
    		haikuTextView.setText("古池や蛙飛びこむ水の音");
    		
    		
    	}else if(34<=allDistance && allDistance<314.5){
    		//栃木
    		whereImageView.setImageResource(R.drawable.tochigi);
    		nowTextView.setText("栃木なう");
    		haikuTextView.setText("あらたうと青葉若葉の日の光 ");
    		
    	}else if(314.5<=allDistance && allDistance<472){
    		//福島
    		whereImageView.setImageResource(R.drawable.fukushima);
    		nowTextView.setText("福島なう");
    		haikuTextView.setText("風流の初やおくの田植うた ");
    		
    	}else if(472<=allDistance && allDistance<673.5){
    		//宮城
    		whereImageView.setImageResource(R.drawable.miyagi);
    		nowTextView.setText("宮城なう");
    		haikuTextView.setText("松嶋やさてまつしまや松嶋や  ");
    		
    	}else if(673.5<=allDistance && allDistance<786.5){
    		//岩手
    		whereImageView.setImageResource(R.drawable.iwate);
    		nowTextView.setText("岩手なう");
    		haikuTextView.setText("夏草や兵どもが夢の跡 ");
    		
    	}else if(786.5<=allDistance && allDistance<810.5){
        	//宮城
        	whereImageView.setImageResource(R.drawable.miyagi);
        	nowTextView.setText("宮城なう");
        	haikuTextView.setText("あかあかと日はつれなくも秋の風 ");
        		
    		
    	}else if(810.5<=allDistance && allDistance<849.5){
    		//秋田
    		whereImageView.setImageResource(R.drawable.akita);
    		nowTextView.setText("秋田なう");
    		haikuTextView.setText("象潟や雨に西施がねぶの花 ");
    		
    	}else if(849.5<=allDistance && allDistance<1154){
    		//山形
    		whereImageView.setImageResource(R.drawable.yamagata);
    		nowTextView.setText("山形なう");
    		haikuTextView.setText("暑き日を海にいれたり最上川");
    		
    	}else if(1154<=allDistance && allDistance<1192){
    		//秋田
    		whereImageView.setImageResource(R.drawable.akita);
    		nowTextView.setText("秋田なう");
    		haikuTextView.setText("汐越や鶴はぎぬれて海涼し");
    		
    	}else if(1192<=allDistance && allDistance<1270){
    		//山形
    		whereImageView.setImageResource(R.drawable.yamagata);
    		nowTextView.setText("山形なう");
    		haikuTextView.setText("閑さや岩にしみ入蝉の声");
    		
    	}else if(1270<=allDistance && allDistance<1635.5){
    		//新潟
    		whereImageView.setImageResource(R.drawable.niigata);
    		nowTextView.setText("新潟なう");
    		haikuTextView.setText("文月や六日も常の夜には似ず");
    		
    	}else if(1635.5<=allDistance && allDistance<1722.5){
    		//富山
    		whereImageView.setImageResource(R.drawable.toyama);
    		nowTextView.setText("富山なう");
    		haikuTextView.setText("早稲の香や分け入る右は有磯海 ");
    		
    	}else if(1722.5<=allDistance && allDistance<1927){
    		//石川
    		whereImageView.setImageResource(R.drawable.ishikawa);
    		nowTextView.setText("石川なう");
    		haikuTextView.setText("石山の石より白し秋の風");
    		
    	}else if(1927<=allDistance && allDistance<2089.5){
    		//福井
    		whereImageView.setImageResource(R.drawable.fukui);
    		nowTextView.setText("福井なう");
    		haikuTextView.setText("物書て扇引さく余波哉");
    		
    	}else if(2089.5<=allDistance && allDistance<2106.5){
    		//岐阜
    		whereImageView.setImageResource(R.drawable.gifu);
    		nowTextView.setText("岐阜なう");
    		haikuTextView.setText("蛤のふたみに別れ行秋ぞ ");
    		
    	}
    	
    	
    }
}

