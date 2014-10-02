package ch.ez_gaming.groessenmesser;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorHandler implements  SensorEventListener{

	//<------- Too much math for my head --> copied code from hsr appquest
	private final float[] magneticFieldData = new float[3];
	private final float[] accelerationData = new float[3];
	private double currentRotationValue;
 
	@Override
	public void onSensorChanged(SensorEvent event) {
 
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			System.arraycopy(event.values, 0, accelerationData, 0, 3);
		}
 
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			System.arraycopy(event.values, 0, magneticFieldData, 0, 3);
		}
	}
 
	public double getCurrentRotationValue() {
		float[] rotationMatrix = new float[16];
 
		if (SensorManager.getRotationMatrix(rotationMatrix, null, accelerationData, magneticFieldData)) {
 
			float[] orientation = new float[4];
			SensorManager.getOrientation(rotationMatrix, orientation);
 
			double dregree = Math.toDegrees(orientation[2]);
 
			return Math.abs(dregree);
		}
 
		return 0;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	public void setCurrentRotationValue(double currentRotationValue) {
		this.currentRotationValue = currentRotationValue;
	}

}
