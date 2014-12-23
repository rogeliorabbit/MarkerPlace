package com.hitglynorthz.markerplace;

import java.util.Locale;

import com.hitglynorthz.markerplace.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hitglynorthz.markerplace.SearchPlace.AddMarkerDialogFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MarkerView extends ActionBarActivity implements OnClickListener {
	private DBManager manager;
	private Cursor cursor;
	private GoogleMap googleMap;
	String marker_tv_id;
	TextView id_tv;
	TextView markername;
	TextView markerdesc;
	private ImageView img_mv;
	String name;
	String desc;
	String imgPath;
    private GoogleMap map;
    double latitude;
    double longitude;
    LatLng latLng;
    private Button btn_openmap;
    private Button btn_opennav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marker_view);
        
        // Botones
        btn_openmap = (Button) findViewById(R.id.btn_openmap);
        btn_openmap.setOnClickListener(this);
        btn_opennav = (Button) findViewById(R.id.btn_opennav);
        btn_opennav.setOnClickListener(this);
        
        // Iniciamos el mapa y le decimos mediante map_settings que no haya zoom, scroll, etc. para mostrar un mapa "plano"
        googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        UiSettings map_settings = googleMap.getUiSettings();
        map_settings.setScrollGesturesEnabled(false);
        map_settings.setZoomControlsEnabled(false);
        map_settings.setZoomGesturesEnabled(false);
        map_settings.setRotateGesturesEnabled(false);
        map_settings.setCompassEnabled(false);
        map_settings.setTiltGesturesEnabled(false);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        // Recibimos la información necesaria para buscar en la BD el marker y mostramos su información
	    marker_tv_id = getIntent().getStringExtra("id");
	    manager = new DBManager(this);
	    cursor = manager.searchMarkerID(marker_tv_id);
	    cursor.moveToFirst();
	    name = cursor.getString(1);
	    desc = cursor.getString(2);
	    latitude = cursor.getDouble(3);
	    longitude = cursor.getDouble(4);
	    imgPath = cursor.getString(5);
	    cursor.close();
	    id_tv = (TextView) findViewById(R.id.marker_tv_id);
	    markername = (TextView) findViewById(R.id.marker_placename);
	    markerdesc = (TextView) findViewById(R.id.marker_placedesc);
	    img_mv = (ImageView) findViewById(R.id.img_mv);
	    id_tv.setText(latitude+ ", " + longitude);
	    markername.setText(name);
	    markerdesc.setText(desc);
		Display display = getWindowManager().getDefaultDisplay();
		int width = 400;
		if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
			width = display.getWidth();
		}else{
			Point size = new Point();
			getWindowManager().getDefaultDisplay().getSize(size);
			width = size.x;
		}
		if(imgPath.equals("res/drawable/backdemo.png")) {
			img_mv.setImageDrawable(getResources().getDrawable(R.drawable.backdemo));
		}else{
			Bitmap resizedBitmap = decodeSampledBitmapFromFile(imgPath, width);
			img_mv.setImageBitmap(resizedBitmap);
		}
        setTitle(name);
	    
	    // Miramos si está Google Play Services disponible en el dispositivo
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if(status!=ConnectionResult.SUCCESS){ 
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        }else {
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            map = fm.getMap();
            latLng = new LatLng(latitude, longitude);
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            map.animateCamera(CameraUpdateFactory.zoomTo(15));
            Marker markerLocation = map.addMarker(new MarkerOptions().position(latLng).title(name));
            markerLocation.showInfoWindow();           
        }     
    }
	
    // Estas dos funciones son las mismas que en MarkerSave, para reescalar la imagen según el ancho de la pantalla del dispositivo
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth) {
	    final int width = options.outWidth;
	    int inSampleSize = +1;
	    if (width > reqWidth) {
	    	inSampleSize = Math.round((float) width / (float) reqWidth);
	    }
	    return inSampleSize;
	}
	
	public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth) {
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path, options);

	    options.inSampleSize = calculateInSampleSize(options, reqWidth);

	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(path, options);
	}
    
	// Funciones para los botones
    public void btn_delete() {
    	DialogFragment dialogFragment = new AddMarkerDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "");
    }
    
    public void btn_edit() {
    	Toast.makeText(getApplicationContext(),"Editar " + name, Toast.LENGTH_LONG).show();
    	Intent intent = new Intent(this, MarkerEdit.class);
 	   	intent.putExtra("id", String.valueOf(marker_tv_id));
 	   	intent.putExtra("name", name);
 	   	intent.putExtra("desc", desc);
 	   	intent.putExtra("lat", String.valueOf(latitude));
 	   	intent.putExtra("lng", String.valueOf(longitude));
 	   	intent.putExtra("img", imgPath);
 	   	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
 	   	startActivity(intent);
 	   	this.finish();
    }
    
    public void onClick(View view) {
    	switch(view.getId()) {
    		case R.id.btn_openmap:
        			String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
        			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        			startActivity(intent);
    		break;
    		case R.id.btn_opennav:
        			Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + ", " + longitude);
        			Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        			mapIntent.setPackage("com.google.android.apps.maps");
        			startActivity(mapIntent);
    		break;
    	}
    }
    
    // Dialog para preguntar si se está seguro de borrar un sitio
    public class AddMarkerDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.delete_marker_ask)
                   .setPositiveButton(R.string.delete_marker, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
	                       	manager.deleteMarker(marker_tv_id);
	                 	   	Intent intent = new Intent(MarkerView.this, MainActivity.class);
	                 	   	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	                 	    startActivity(intent);
	                 	    MarkerView.this.finish();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view, menu);
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
        case R.id.action_delete:
        	btn_delete();
        	return true;
        case R.id.action_edit:
        	btn_edit();
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
}
