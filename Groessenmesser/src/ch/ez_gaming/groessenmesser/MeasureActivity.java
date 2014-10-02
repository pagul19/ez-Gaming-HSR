package ch.ez_gaming.groessenmesser;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class MeasureActivity extends Activity implements Callback {

	private Camera cam;
	private SurfaceHolder camHolder;
	private SensorManager sm;
	private Sensor magnetFS, accellS;
	private SensorHandler sHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//<--------Fullscreen stizzle
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //until here ----->
		setContentView(R.layout.activity_measure);
		
		sm = (SensorManager)getSystemService(SENSOR_SERVICE);
		magnetFS = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		accellS = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		sHandler = new SensorHandler();
		sm.registerListener(sHandler, magnetFS, SensorManager.SENSOR_DELAY_NORMAL);
		sm.registerListener(sHandler, accellS, SensorManager.SENSOR_DELAY_NORMAL);
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(cam != null) {
			cam.stopPreview();
			cam.release();
		}
			
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		SurfaceView sfv = (SurfaceView)findViewById(R.id.surfaceV1);
		camHolder = sfv.getHolder();
		camHolder.addCallback(this);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.measure, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		cam = Camera.open();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		cam.stopPreview();
		cam.setDisplayOrientation(90);
		
		Camera.Parameters parms = cam.getParameters();
		Camera.Size size = parms.getPreviewSize();
		parms.setPreviewSize(size.width, size.height);
		cam.setParameters(parms);
		
		try {
			cam.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		cam.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {}
	
	public void onButton() {
		Intent d = new Intent(); //empty intent to send the double value back
		d.putExtra("DEGREE", sHandler.getCurrentRotationValue());
		setResult(RESULT_OK,d);
	}
}
