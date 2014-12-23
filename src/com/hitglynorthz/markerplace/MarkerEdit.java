package com.hitglynorthz.markerplace;

import com.hitglynorthz.markerplace.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MarkerEdit extends ActionBarActivity {
	EditText name_edittext;
	EditText desc_edittext;
	EditText lat_edittext;
	EditText lng_edittext;
	String marker_tv_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.add_marker);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    	findViewById(R.id.btn_img).setVisibility(View.GONE);
    	findViewById(R.id.btn_galeria).setVisibility(View.GONE);

	    final String latString = getIntent().getStringExtra("lat");
	    final String lngString = getIntent().getStringExtra("lng");
	    final String name = getIntent().getStringExtra("name");
	    final String desc = getIntent().getStringExtra("desc"); 

	    lat_edittext = (EditText) findViewById(R.id.editText_lat);
	    lng_edittext = (EditText) findViewById(R.id.editText_lng);
	    name_edittext = (EditText) findViewById(R.id.editText_name);
	    desc_edittext = (EditText) findViewById(R.id.editText_desc);
	    lat_edittext.setFocusable(false);
	    lng_edittext.setFocusable(false);
	    
	    lat_edittext.setText(latString);
	    lng_edittext.setText(lngString);
	    name_edittext.setText(name);
	    desc_edittext.setText(desc);
        setTitle(getString(R.string.marker_edit) + name);
	    
	    Button btSave = (Button) findViewById(R.id.btn_save);
	    btSave.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View view) {
	    		marker_tv_id = getIntent().getStringExtra("id");
	    	    String place_name = name_edittext.getText().toString();
	    	    String place_desc = desc_edittext.getText().toString();
	    	    String imgPatchDB = getIntent().getStringExtra("img");
	        	DBManager managerDB = new DBManager(MarkerEdit.this);
	        	managerDB.editMarker(marker_tv_id, place_name, place_desc, latString, lngString, imgPatchDB);
	    		Toast.makeText(getBaseContext(), getString(R.string.marker_edit_done) + " " + place_name, Toast.LENGTH_SHORT).show();
         	   	Intent intent = new Intent(MarkerEdit.this, MarkerView.class);
         	   	intent.putExtra("id", String.valueOf(marker_tv_id));
         	   	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         	    startActivity(intent);
	    	}
	    });
	    
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
        	finish();
            return true;
        case R.id.action_settings:
    		Intent intent = new Intent(this, Info.class);
    		startActivity(intent);
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }

}