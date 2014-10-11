package com.notnewarai.walkmeter;

import java.util.Date;

import com.notnewarai.walkmeter.util.WalkMeterUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;


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
		        // 歩数情報作成
		        db.execSQL("CREATE TABLE WALK_METER_INFO "
		            + "(TODAY_STEPS INTEGER, REG_DATE TEXT);");
		        stmt = db.compileStatement(
		            "INSERT INTO WALK_METER_INFO VALUES(0, ?);");
		        String date = WalkMeterUtils.dateformat(
		            "yyyyMMddHHmmss", new Date(), 0);
		        stmt.bindString(1, date);
		        stmt.executeInsert();
		 
		        // 歩数履歴作成
		        db.execSQL("CREATE TABLE WALK_METER_HISTORY (HISTORY_DATE TEXT PRIMARY KEY, SUM_STEPS INTEGER)");
		 
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
	        String date = WalkMeterUtils.dateformat(
	            "yyyyMMddHHmmss", new Date(), 0);
	        db.execSQL("UPDATE WALK_METER_INFO SET TODAY_STEPS = ?,REG_DATE = ?; ",
	            new String[] { "" + step, date });
	        db.setTransactionSuccessful();
	    } finally {
	        db.endTransaction();
	    }
	}
}
