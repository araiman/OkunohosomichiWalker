
package com.notnewarai.walkmeter;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;



public class WalkMeterService extends Service implements SensorEventListener {
    public boolean mState = false;

    private SensorManager mSensorManager;

    private float mSensorFrom = 0;

    private int mDispCount = 0;
    
    private int step = 0;
    
    private boolean mCountUp = false;

    public static final String ACTION = "WalkMeterCountUp";
    
    
    
    private WalkMeterSQLiteOpenHelper db;
   

    class WalkMeterBinder extends Binder {
        WalkMeterService getService() {
            return WalkMeterService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
    	db = new WalkMeterSQLiteOpenHelper(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return new WalkMeterBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    public void startCount() {
    	this.db = db;
		int step = db.selectCount();
		this.mDispCount = step;
		
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0)
            mSensorManager.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_UI);
        mState = true;
    }

    public void stopCount() {
        mDispCount = 0;
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this);
        mState = false;
    }

    public int getCounter() {
        return mDispCount;
    }

    public boolean getState() {
        return mState;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float sensorTo = absSensorValue(event);
            if (checkSensorCount(mSensorFrom, sensorTo)) {
                mDispCount++;
                sendBroadcast(new Intent(ACTION));
                db.updateCount(mDispCount);
            }
        }
    }

    private float absSensorValue(SensorEvent event) {
        return Math.abs(event.values[0]) + Math.abs(event.values[1]) + Math.abs(event.values[2]);
    }

    private boolean checkSensorCount(float from, float to) {
        float offset = (float)0.5;
        if (mCountUp) {
            if ((from - offset) > to) {
                mCountUp = false;
                mSensorFrom = to;
            }
        } else {
            if ((from + offset) < to) {
                mCountUp = true;
                mSensorFrom = to;
                return true;
            }
        }
        return false;
    }
    
  
}
