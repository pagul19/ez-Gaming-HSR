package ch.ez_gaming.schatzkarte;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.modules.IArchiveFile;
import org.osmdroid.tileprovider.modules.MBTilesFileArchive;
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.TilesOverlay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity implements LocationListener{
	private MapView mMap;
	private LocationManager locationMan;
	private Location curLoc;
	List<Location> locs = new ArrayList<Location>();
	private MyItemizedOverlay myItemizedOverlay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mMap = (MapView) findViewById(R.id.mapview /*eure ID der Map View */);
		mMap.setTileSource(TileSourceFactory.MAPQUESTOSM);
		 
		mMap.setMultiTouchControls(true);
		mMap.setBuiltInZoomControls(true);
		 
		IMapController controller = mMap.getController();
		controller.setZoom(18);
		 
		// Die TileSource beschreibt die Eigenschaften der Kacheln die wir anzeigen
		XYTileSource treasureMapTileSource = new XYTileSource("mbtiles", ResourceProxy.string.offline_mode, 1, 20, 256, ".png", "http://example.org/");
		 
		File file = new File(Environment.getExternalStorageDirectory() /* entspricht /sdcard/ */, "hsr.mbtiles");
		 
		/* Das verwenden von mbtiles ist leider ein wenig aufwändig, wir müssen
		* unsere XYTileSource in verschiedene Klassen 'verpacken' um sie dann
		* als TilesOverlay über der Grundkarte anzuzeigen.
		*/
		MapTileModuleProviderBase treasureMapModuleProvider = new MapTileFileArchiveProvider(new SimpleRegisterReceiver(this),
		treasureMapTileSource, new IArchiveFile[] { MBTilesFileArchive.getDatabaseFileArchive(file) });
		 
		MapTileProviderBase treasureMapProvider = new MapTileProviderArray(treasureMapTileSource, null,
		new MapTileModuleProviderBase[] { treasureMapModuleProvider });
		 
		TilesOverlay treasureMapTilesOverlay = new TilesOverlay(treasureMapProvider, getBaseContext());
		treasureMapTilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
		 
		// Jetzt können wir den Overlay zu unserer Karte hinzufügen:
		mMap.getOverlays().add(treasureMapTilesOverlay);
		
		locationMan = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);		
		if(!locationMan.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setMessage("ACTIVATE GPS");
			finish();
			
			AlertDialog dialog = b.create();
			dialog.show();
		}
		
		initMarker();
	}
	
	private void initMarker() {
        Drawable marker=getResources().getDrawable(android.R.drawable.star_big_on);
        int markerWidth = marker.getIntrinsicWidth();
        int markerHeight = marker.getIntrinsicHeight();
        marker.setBounds(0, markerHeight, markerWidth, 0);
         
        ResourceProxy resourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
         
        myItemizedOverlay = new MyItemizedOverlay(marker, resourceProxy);
        mMap.getOverlays().add(myItemizedOverlay);
         
        GeoPoint myPoint1 = new GeoPoint(0*1000000, 0*1000000);
        myItemizedOverlay.addItem(myPoint1, "myPoint1", "myPoint1");
        GeoPoint myPoint2 = new GeoPoint(50*1000000, 50*1000000);
        myItemizedOverlay.addItem(myPoint2, "myPoint2", "myPoint2");		
	}

	@Override
	public void onResume() {
		super.onResume();
		locationMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
		locationMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
	}

	@Override
	public void onPause() {
		super.onPause();
		locationMan.removeUpdates(this);
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
	
	public void onButton(View v) {
		locs.add(curLoc);
		markPoint();
	}

	private void markPoint() {
		locs.get(locs.size() - 1);
	}

	@Override
	public void onLocationChanged(Location location) {
		curLoc = location;
	}

	@Override
	public void onProviderDisabled(String provider) {}
	@Override
	public void onProviderEnabled(String provider) {}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
}
