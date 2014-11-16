package com.notnewarai.walkmeter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.widget.TabHost;


public class MainActivity extends FragmentActivity implements FragmentTabHost.OnTabChangeListener {

	 private SharedPreferences preference;
	 private Editor editor;
	 
	
	 
	
	@Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	    
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
        
	    // FragmentTabHost を取得する
	    FragmentTabHost tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
	    tabHost.setup(this, getSupportFragmentManager(), R.id.container);

	    TabHost.TabSpec tabSpec1, tabSpec2, tabSpec3, tabSpec4;

	    // TabSpec1 を生成する
	    tabSpec1 = tabHost.newTabSpec("tab1");
	    tabSpec1.setIndicator("今日");
	    // TabHost に追加
	    tabHost.addTab(tabSpec1, WalkMeterFragment.class, null);

	    // TabSpec2 を生成する
	    tabSpec2 = tabHost.newTabSpec("tab2");
	    tabSpec2.setIndicator("記録");
	    // TabHost に追加
	    tabHost.addTab(tabSpec2, WalkMeterRecordFragment.class, null);
	    
	    // TabSpec3 を生成する
	    tabSpec3 = tabHost.newTabSpec("tab3");
	    tabSpec3.setIndicator("今どこ");
	    // TabHost に追加
	    tabHost.addTab(tabSpec3, WalkMeterWhereFragment.class, null);
	    
	    // TabSpec4 を生成する
	    tabSpec4 = tabHost.newTabSpec("tab4");
	    tabSpec4.setIndicator("道のり");
	    // TabHost に追加
	    tabHost.addTab(tabSpec4, WalkMeterStrokeFragment.class, null);

	    // リスナー登録
	    tabHost.setOnTabChangedListener(this);
	  }
	
	@Override
	public void onTabChanged(String tabId) {
		Log.d("onTabChanged", "tabId: " + tabId);
		
	}

}