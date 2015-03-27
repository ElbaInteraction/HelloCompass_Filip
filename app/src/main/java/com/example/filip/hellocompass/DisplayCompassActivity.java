package com.example.filip.hellocompass;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;


public class DisplayCompassActivity extends Activity implements SensorEventListener {

    Float azimut;  // View to draw a compass

    public class CustomDrawableView extends View {
        Paint paint = new Paint();

        public CustomDrawableView(Context context) {
            super(context);
            paint.setStyle(Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setAntiAlias(true);
            paint.setTextSize(40f);
        }

        protected void onDraw(Canvas canvas) {
            int width = getWidth();
            int height = getHeight();
            int centerx = width / 2;
            int centery = height / 2;

            paint.setStyle(Style.FILL);
            paint.setColor(Color.LTGRAY);
            canvas.drawRect(0, 0, width, height, paint);

            paint.setColor(Color.GRAY);
            canvas.drawCircle(centerx, centery, width/3, paint);
            paint.setStyle(Style.STROKE);

            paint.setColor(Color.BLUE);
            canvas.drawLine(centerx, centery - width / 4, centerx, centery, paint); //arrow line


            canvas.drawLine(centerx, centery - width / 3, centerx + 50, centery - width / 4, paint); //arrowhead right
            canvas.drawLine(centerx, centery - width / 3, centerx - 50, centery - width / 4, paint); //arrowhead left
            canvas.drawLine(centerx + 50, centery - width / 4, centerx - 50, centery - width / 4, paint); //arrowhead bottom

            canvas.drawCircle(centerx, centery, width / 3, paint);

            // Rotate the canvas with the azimut
            if (azimut != null)
                canvas.rotate(-azimut * 360 / (2 * 3.14159f), centerx, centery);

            paint.setColor(Color.DKGRAY);
            canvas.drawLine(centerx, centery + width / 3, centerx, centery - width / 3, paint);
            canvas.drawLine(centerx + width / 3, centery, centerx - width / 3, centery, paint);

            //draw text directions.
            paint.setColor(Color.RED);

            canvas.drawText("N", centerx - width / 60, centery - width / 3 - 2, paint);

            canvas.rotate(90, centerx, centery);
            canvas.drawText("E", centerx - width / 60, centery - width / 3 - 2, paint);

            canvas.rotate(90, centerx, centery);
            canvas.drawText("S", centerx - width / 60, centery - width / 3 - 2, paint);

            canvas.rotate(90, centerx, centery);
            canvas.drawText("W", centerx - width / 60, centery - width / 3 - 2, paint);

            paint.setColor(Color.BLUE);
            paint.setStyle(Style.FILL);
            canvas.drawCircle(centerx, centery, 10, paint); //middle circle
            paint.setStyle(Style.STROKE);


        }
    }

    CustomDrawableView mCustomDrawableView;
    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCustomDrawableView = new CustomDrawableView(this);
        setContentView(mCustomDrawableView);    // Register the sensor listeners
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    float[] mGravity;
    float[] mGeomagnetic;

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimut = orientation[0]; // orientation contains: azimut, pitch and roll
            }
        }
        mCustomDrawableView.invalidate();
    }
}
