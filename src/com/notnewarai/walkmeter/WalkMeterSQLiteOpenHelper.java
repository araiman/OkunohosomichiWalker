package com.notnewarai.walkmeter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.notnewarai.walkmeter.util.WalkMeterUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;


public class WalkMeterSQLiteOpenHelper extends SQLiteOpenHelper {
	static final private String DBNAME = "WALKMETER2";
	static final private int VERSION = 1;
	
	public WalkMeterSQLiteOpenHelper(Context context){
		super(context, DBNAME, null, VERSION);
	}
	
		@Override
		public void onCreate(SQLiteDatabase db) {
		    db.beginTransaction();
		    try {
		    	 SQLiteStatement stmt;
			        SQLiteStatement stmt2;
			        // 歩数情報作成
			        db.execSQL("CREATE TABLE WALK_METER_INFO "
			            + "(TODAY_STEPS INTEGER, REG_DATE TEXT);");
			        stmt = db.compileStatement(
			            "INSERT INTO WALK_METER_INFO VALUES(0, ?);");
			        
			        Date cDate = new Date();
			        String date = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
			        
			        stmt.bindString(1, date);
			        stmt.executeInsert();
			 
			        // 歩数履歴作成
			        db.execSQL("CREATE TABLE WALK_METER_HISTORY (HISTORY_DATE TEXT, HISTORY_STEPS INTEGER);");
			        
			        stmt2 = db.compileStatement(
				            "INSERT INTO WALK_METER_HISTORY VALUES(0, ?);");
			        stmt2.bindString(1, date);
			        stmt2.executeInsert();
			        
			        db.setTransactionSuccessful();

		    } finally {
		        db.endTransaction();
		    }
		}
	
	@Override 
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2){
		
	}
	
	/**
	 * 現在歩数を取得。
	 * @return 現在歩数
	 */
	public int selectCount() {
	    final SQLiteDatabase db = this.getReadableDatabase();
	    Cursor c = db.rawQuery("SELECT TODAY_STEPS FROM WALK_METER_INFO ORDER BY REG_DATE DESC;", null);
	    c.moveToFirst();
	    int step = c.getInt(0);
	    c.close();
	    
	    return step;
	}
	
	/**
	 * 歩数の更新
	 * @param step 更新歩数
	 */
	public void updateCount(int step) {
	    final SQLiteDatabase db = this.getReadableDatabase();
	    
	    	db.beginTransaction();
		    try {
		        db.execSQL("UPDATE WALK_METER_INFO SET TODAY_STEPS = ?; ",
		            new String[] { "" + step });
		        db.setTransactionSuccessful();
		    } finally {
		        db.endTransaction();
		    }
		    
	}

	public void insertCount(){
	    final SQLiteDatabase db = this.getReadableDatabase();	
	    Cursor c = db.rawQuery("SELECT TODAY_STEPS,REG_DATE FROM WALK_METER_INFO ORDER BY REG_DATE DESC;", null);
	    c.moveToFirst();
	    int step = c.getInt(0);
	    String date = c.getString(1);
	    c.close();
	    
	    Date cDate = new Date();
        String strcDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);	    
	    
	    	db.beginTransaction();
		    try {
		    	
		    	db.execSQL("INSERT INTO WALK_METER_HISTORY (HISTORY_STEPS, HISTORY_DATE) VALUES (?,?); ",
		    			new String[] { "" + step, date });
		        db.execSQL("UPDATE WALK_METER_INFO SET TODAY_STEPS = ?,REG_DATE = ?; ",
			            new String[] { "" + 0, strcDate });
		        
		        
		        db.setTransactionSuccessful();
		    } finally {
		        db.endTransaction();

		    }
	    }
	
	public String getDate(){
		final SQLiteDatabase db = this.getReadableDatabase();
	    
	    Cursor c = db.rawQuery("SELECT REG_DATE FROM WALK_METER_INFO;", null);
	    c.moveToFirst();
	    String reg_Date = c.getString(0);
	    return reg_Date;
	}
	
	public int getAllHistorySteps(){
		Log.d("getAllHistorySteps","started");
		ArrayList<Integer> all_history_steps= new ArrayList<Integer>();
		int sum_history_steps = 0;
		
		final SQLiteDatabase db = this.getReadableDatabase();
		
		
		// テーブルからデータを検索
		Cursor cursor = db.rawQuery("SELECT HISTORY_STEPS FROM WALK_METER_HISTORY;",null);
		Log.d("cursor",""+cursor);
		// 参照先を先頭に
		boolean exist = cursor.moveToFirst();
		// 全歩数を取得
		while(exist) {
		
		all_history_steps.add(cursor.getInt(cursor.getColumnIndex("HISTORY_STEPS")));
		exist = cursor.moveToNext();
		}
		// 閉じる
		cursor.close();
		Log.d("allHistorySteps",""+all_history_steps);	
		Log.d("allHistorySteps.size",""+all_history_steps.size());
		for(int i=0;i<all_history_steps.size();i++){
			sum_history_steps += all_history_steps.get(i); 
		}
		
		return sum_history_steps;

	}
	
}
	
	

