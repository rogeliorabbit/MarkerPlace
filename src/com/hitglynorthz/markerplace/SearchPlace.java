package com.hitglynorthz.markerplace;

import java.io.IOException;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hitglynorthz.markerplace.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SearchPlace extends ActionBarActivity implements LocationListener, OnMapLongClickListener, OnInfoWindowClickListener{

	 private GoogleMap googleMap;
	 MarkerOptions markerOptions;
	 LatLng latLng;
	 Marker m;
	 double lat;
	 double lng;
	 private LocationManager locationManager;
	 private static final long MIN_TIME = 400;
	 private static final float MIN_DISTANCE = 1000;
	 SearchView searchView;
	 SearchManager searchManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.map_layout);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
	    
	    // Cargamos el mapa
	    if (googleMap == null) {
	    	googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	    	Toast.makeText(getApplicationContext(), getResources().getString(R.string.loadu), Toast.LENGTH_LONG).show();
	        if (googleMap != null) {
	        	//Acción si no se carga el mapa
	            //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
	        }
	    }
	    googleMap.setMyLocationEnabled(true);
	    googleMap.setOnMapLongClickListener(this); 
	    googleMap.setOnInfoWindowClickListener(this);

	    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); 
	    
	    // Si el usuario usa el buscador se ejecuta la busqueda
        Button btn_find = (Button) findViewById(R.id.btn_find);
        OnClickListener findClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etLocation = (EditText) findViewById(R.id.et_location);
                String location = etLocation.getText().toString();
                if(location!=null && !location.equals("")){
                	// Llamamos a GeocoderTask para que realice la busqueda en segundo plano
                    new GeocoderTask().execute(location);
                }
                try {
                	InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }catch(Exception e) {
                	
                }  
            }
        };
        btn_find.setOnClickListener(findClickListener);

	}

	// Se busca la localización del usuario
	@Override
	public void onLocationChanged(Location location) {
	    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    	lat = location.getLatitude();
    	lng = location.getLongitude();
	    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
	    googleMap.animateCamera(cameraUpdate);
	    locationManager.removeUpdates(this);

	}
	
	// Clase para la busqueda de localización según lo que el usuario escribió en el EditText
	// Con AsyncTask hacemos que la busqueda sea en segundo plano (doInBackground) y no se cuelgue
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>>{
 
        @Override
        protected List<Address> doInBackground(String... locationName) {
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;
 
            try {
                // Se busca un máximo de tres lugares que coincidan con la busqueda realizada por el usuario
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }
 
        @Override
        protected void onPostExecute(List<Address> addresses) {
 
            if(addresses==null || addresses.size()==0){
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }
            googleMap.clear();
            for(int i=0;i<addresses.size();i++){
            	Address address = (Address) addresses.get(i);
            	lat = address.getLatitude();
            	lng = address.getLongitude();
                latLng = new LatLng(address.getLatitude(), address.getLongitude());
 
                String addressText = String.format("%s, %s",
                address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                address.getCountryName());
 
                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressText);
                markerOptions.snippet("Latitude:" +  address.getLatitude()  + ", Longitude:"+ address.getLongitude() );
 
                m = googleMap.addMarker(markerOptions);
                
                if(i==0) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }
            }
        }
    }
    
    // Miramos si el usuario hace un longclick para poner un marker
    @Override
    public void onMapLongClick(LatLng point) {
    	if(m != null) {
    		m.setPosition(point);
    	}else{
    		latLng = point;
    		m = googleMap.addMarker(new MarkerOptions().position(point).title(point.toString()).draggable(true).snippet("Latitude:" +  point.toString() ));
    	}
    }
    
    // Clase para responder a la pulsación en un marker
    @Override
    public void onInfoWindowClick(Marker marker) {
    	DialogFragment dialogFragment = new AddMarkerDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "");
    }
    
    // Clase para lanzar un dialog al pulsar en la información de un marker
    public class AddMarkerDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.add_marker_title)
                   .setPositiveButton(R.string.add_marker, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                    	   // Si se acepta el dialogo
                    	   //Toast.makeText(getBaseContext(), "New marker added@ " + latLng, 0).show();
                    	   Intent intent = new Intent(SearchPlace.this, MarkerSave.class);
                    	   intent.putExtra("latString", Double.toString(lat));
                    	   intent.putExtra("lngString", Double.toString(lng));
	                 	   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    	   startActivity(intent);
                       }
                   })
                   .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           // Si se cancela el dialogo
                       }
                   });
            return builder.create();
        }
    }

	@Override
	public void onProviderDisabled(String provider) {
	    // TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
	    // TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	    // TODO Auto-generated method stub

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
