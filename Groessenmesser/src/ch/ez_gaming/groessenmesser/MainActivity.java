package ch.ez_gaming.groessenmesser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity {

	private Button mMeasureB, mCalcB, mResetB; //google convention to name imported widgets from xml with an "m" prefix
	private EditText mDisTf;
	private TextView mAlphaLa, mBetaLa, mResLa;
	
	final static int DEGREE_REQUEST_CODE = 0;
	
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
    
    //executed on button press
    public void onButton(View view) {
    	switch (view.getId()) {
    		case R.id.measureB: 
    			Intent i = new Intent(this, MeasureActivity.class);
    			startActivityForResult(i, DEGREE_REQUEST_CODE);
    			break;
    		case R.id.calB:
    			calculate();
    		case R.id.resetB:
    			mCalcB.setEnabled(false);
    		default:
    			break;
    	}
    }
    
    private void calculate() {
		// TODO Auto-generated method stub
		
	}


	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DEGREE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
            	mMeasureB.setEnabled(false);
            	double dAngledata = data.getDoubleExtra("ANGLE", 90);
            	mAlphaLa.setText(String.format("The angle alpha is: %.2f", dAngledata));
            	mBetaLa.setText(String.format("The angle alpha is: %.2f", dAngledata));
            }
        }
    }
}
