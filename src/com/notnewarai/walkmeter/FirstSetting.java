package com.notnewarai.walkmeter;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.content.Intent;

public class FirstSetting extends PreferenceActivity{

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        if(intent != null){
        	 // 読み書きするプリファレンスのファイル名を指定
            PreferenceManager prefMgr = getPreferenceManager();
            prefMgr.setSharedPreferencesName("name_and_height");

            // 定義した設定項目XMLを読み込む
            addPreferencesFromResource(R.layout.preference);
        }

       
    }	
}