package ch.ez_gaming.groessenmesser;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	private Button mMeasureB, mCalcB, mResetB; //google convention to name imported widgets from xml with an "m" prefix
	private EditText mDisTf;
	private TextView mAlphaLa, mBetaLa, mResLa;
	private double dAlpha, dBeta, dRes;
	
	final static int DEGREE_REQUEST_CODE_1 = 0;
	final static int QR_REQUEST_CODE = 1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMeasureB = (Button)findViewById(R.id.measureB); //button to start measure activity
        mCalcB = (Button)findViewById(R.id.calB); //button to calculate
        mResetB = (Button)findViewById(R.id.resetB); //button to reset all fields
        
        mDisTf = (EditText)findViewById(R.id.disTf); //Textfield for input: distance from the object (given)
        mAlphaLa = (TextView)findViewById(R.id.aplhaLa); //label to display the aplha angle
        mBetaLa	= (TextView)findViewById(R.id.betaLa); //label to display the beta angle
        mResLa = (TextView)findViewById(R.id.resLa); //label to display the result in cm¨
        
        mCalcB.setEnabled(false);
        mResetB.setEnabled(false);
        mDisTf.setEnabled(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    MenuItem menuItem = menu.add("Log");
	    menuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
		    @Override
		    public boolean onMenuItemClick(MenuItem item) {
			    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			    startActivityForResult(intent, QR_REQUEST_CODE);
			    return false;
		    }
	    });
     
    return super.onCreateOptionsMenu(menu);
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
    
    //executed on button press
    public void onButton(View view) {
    	switch (view.getId()) {
    		case R.id.measureB: 
    			Intent i = new Intent(this, MeasureActivity.class);
    			startActivityForResult(i, DEGREE_REQUEST_CODE_1);
    			break;
    		case R.id.calB:
    			calculate();
    			break;
    		case R.id.resetB:
    			mCalcB.setEnabled(false);
    	        mResetB.setEnabled(false);
    			mMeasureB.setEnabled(true);
    			dAlpha = 0;
    			dBeta = 0;
    			mAlphaLa.setText("");
    			mBetaLa.setText("");
    			mResLa.setText("");
    			mDisTf.setText("");
    			break;
    		default:
    			break;
    	}
    }
    
    private void calculate() {
    	String distanceEntered = mDisTf.getText().toString();
    	if(distanceEntered != null && !distanceEntered.isEmpty()) {
    		double dDistanceEntered = Double.parseDouble(distanceEntered);
        	double c = dDistanceEntered/Math.sin(Math.toRadians(dAlpha));
        	double dGamma = 180.0 - dAlpha - dBeta;
        	dRes = Math.sin(Math.toRadians(dBeta)) * c / Math.sin(Math.toRadians(dGamma));
        	mResLa.setText(String.format("The object is %.2f Meters high", dRes));
        	mCalcB.setEnabled(false);
    	}
	}
    
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DEGREE_REQUEST_CODE_1) {
            if (resultCode == RESULT_OK) {
            	mMeasureB.setEnabled(false);
            	dAlpha = data.getDoubleExtra("ALPHA", 90);
            	mAlphaLa.setText(String.format("The angle alpha is: %.2f", dAlpha));
            	double tempD = data.getDoubleExtra("BETA", 90);
            	dBeta = tempD - dAlpha;
            	mBetaLa.setText(String.format("The beta alpha is: %.2f", dBeta));
    			mMeasureB.setEnabled(false);
    			mCalcB.setEnabled(true);
    	        mResetB.setEnabled(true);
    	        mDisTf.setEnabled(true);
            }
        } else 	if (requestCode == QR_REQUEST_CODE) {
        	if (resultCode == RESULT_OK) {
        		String logMsg = data.getStringExtra("SCAN_RESULT");
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
		CharSequence calculatedObjectHeight = String.format("%.2f", dRes);
		// Achtung, je nach App wird etwas anderes eingetragen (siehe Tabelle ganz unten):
		intent.putExtra("ch.appquest.logmessage", qrCode + ": " + calculatedObjectHeight);
		 
		startActivity(intent);
	}
}
