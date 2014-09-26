package ch.hsr.metaldetector;

import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements SensorEventListener{
	private ProgressBar mProgBar; 
	private TextView mTextview;
	private SensorManager sm;
	private Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgBar = (ProgressBar)findViewById(R.id.pBarMagnetic);
        mTextview = (TextView)findViewById(R.id.valuesMagnetic);
        sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mProgBar.setMax((int) sensor.getMaximumRange());
        
    }

   
    @Override
    protected void onPause() {
    	super.onPause();
    	sm.unregisterListener(this);
    }
    @Override
    protected void onResume() {
    	super.onResume();
    	sm.registerListener(this, sensor, sm.SENSOR_DELAY_NORMAL);
    	
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
    public void onSensorChanged(SensorEvent event) {
    	float[] mag = event.values;
    	double dProg = FloatMath.sqrt(mag[0] * mag[0] + mag[1] * mag[1] + mag[2] * mag[2]);
    	this.mProgBar.setProgress((int) dProg);
    	this.mTextview.setText((int) dProg + "/" + sensor.getMaximumRange());
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}


	private static final int SCAN_QR_CODE_REQUEST_CODE = 0;
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem menuItem = menu.add("Log");
		menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
	 
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
				startActivityForResult(intent, SCAN_QR_CODE_REQUEST_CODE);
				return false;
			}
		});
	 
		return super.onCreateOptionsMenu(menu);
	}
	 
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == SCAN_QR_CODE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				String logMsg = intent.getStringExtra("SCAN_RESULT");
				log(logMsg);
			}
		}
	}
	
	private void log(String qrCode) {
		Intent intent = new Intent("ch.appquest.intent.LOG");
	 
		if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
			Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG).show();
			return;
		}
	 
		intent.putExtra("ch.appquest.taskname", "Grössen Messer");
		intent.putExtra("ch.appquest.logmessage", qrCode);
	 
		startActivity(intent);
	}
}
