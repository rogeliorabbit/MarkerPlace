package com.hitglynorthz.markerplace;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hitglynorthz.markerplace.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsMarkerView extends ActionBarActivity {
    private GoogleMap map;
    LatLng latLng;
	private DBManager manager;
	private Cursor cursor;
	String name;
	private String idmrkr;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_marker_view);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
		findViewById(R.id.tv_snippet).setVisibility(View.GONE);
		findViewById(R.id.btn_mkview).setVisibility(View.INVISIBLE);
        
        // Miramos si esta Google Play Services instalado en el dispositivo. Si es correcto lanzamos el mapa, si no mostramos un error
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if(status!=ConnectionResult.SUCCESS){
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
         }else {
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.loadmap), Toast.LENGTH_SHORT).show();
            
            manager = new DBManager(this);
    	    cursor = manager.getAllMarkers();
    	    if(cursor.getCount() == 0) {
    	    	TextView sp = (TextView) findViewById(R.id.name_place);
    	    	sp.setText("No tienes markers");    	    	
    	    }
    	    if(cursor.getCount()!=0) {
	    	    if(cursor.moveToFirst()) {
	    	    	do {
	    	    		idmrkr = cursor.getString(cursor.getColumnIndex(DBManager.CN_ID));
			    	    name = cursor.getString(cursor.getColumnIndex(DBManager.CN_PLACENAME)); 
			    	    latitude = cursor.getDouble(cursor.getColumnIndex(DBManager.CN_LAT)); 
			    	    longitude = cursor.getDouble(cursor.getColumnIndex(DBManager.CN_LNG));

			            map = fm.getMap();
			            latLng = new LatLng(latitude, longitude);
			            Marker markerLocation = map.addMarker(new MarkerOptions().position(latLng).title(name).snippet(idmrkr));
		    	    	map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
		                    @Override
		                    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker)
		                    {
		                        marker.showInfoWindow();
		            			findViewById(R.id.btn_mkview).setVisibility(View.VISIBLE);
		                        btn_refresh(marker.getTitle(), marker.getSnippet());
		                        return true;
		                    }
		                });
	    	    	}while(cursor.moveToNext());
	    	    }
	            cursor.close(); 
    	    }
            
        }
        
    }
    
    // Refrescamos el TextView cada vez que se pulsa en un marker mostrando su titulo
    private void btn_refresh(String name, String snippet) {
    	TextView nm = (TextView) findViewById(R.id.name_place);
    	nm.setText(name);
    	TextView sp = (TextView) findViewById(R.id.tv_snippet);
    	sp.setText(snippet);
    }
    
    // Lanzamos MarkerView para mostrar la información completa desde un marker seleccionado
    public void btn_mkview(View v) {
    	Intent intent = new Intent(MapsMarkerView.this, MarkerView.class);
    	TextView sp = (TextView) findViewById(R.id.tv_snippet);
    	String id = sp.getText().toString();
 	   	intent.putExtra("id", String.valueOf(id));
 	   	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
 	    startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        case R.id.action_settings:
    		Intent intent = new Intent(this, Info.class);
    		startActivity(intent);
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
}

