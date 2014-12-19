package com.hitglynorthz.markerplace;

import com.hitglynorthz.markerplace.R;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements OnClickListener {
	private DBManager manager;
	private Cursor cursor;
	private ListView listMarkers;
	private SimpleCursorAdapter adapter;
	private Button btn_map;
	private Button btn_mpmrkrs;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main_layout);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        
        // Botones
        btn_map = (Button) findViewById(R.id.btn_map);
        btn_map.setOnClickListener(this);
        btn_mpmrkrs = (Button) findViewById(R.id.btn_mpmrkrs);
        btn_mpmrkrs.setOnClickListener(this);
	    
	    // Iniciamos la BD
        DBHelper helper = new DBHelper(this);
        helper.getWritableDatabase();
	    
	    manager = new DBManager(this);
	    listMarkers = (ListView) findViewById(R.id.listMarkers);
	    cursor = manager.getAllMarkers();
	    String[] from = new String[]{DBManager.CN_ID , DBManager.CN_PLACENAME, DBManager.CN_PLACEDESC};
        int[] to = new int[]{R.id.text1, R.id.text2, R.id.text3};
	    
        // Miramos si el cursor tiene datos y los mostramos, si no mostramos un mensaje avisando de que está vacío
        if(cursor.getCount()!=0) {
        	findViewById(R.id.info1).setVisibility(View.GONE);
    	    adapter = new SimpleCursorAdapter(this, R.layout.row_layout, cursor, from, to, 0);
    	    listMarkers.setAdapter(adapter);
    	    listMarkers.setOnItemClickListener(new OnItemClickListener() {
    	    	@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             	   	Intent intent = new Intent(MainActivity.this, MarkerView.class);
             	   	intent.putExtra("id", String.valueOf(id));
             	    startActivity(intent);
				}
    	    });
        }else{
        	TextView ed = (TextView) findViewById(R.id.my_markers);
        	ed.setText(R.string.no_markers);
        }
	    
	}
	
	// Leemos los onClick de los botones y lanzamos los intents
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btn_map:
		 	   	Intent intent_map = new Intent(this, SearchPlace.class);
		 	    startActivity(intent_map);
				break;
			case R.id.btn_mpmrkrs:
		 	   	Intent intent_mpmrkrs = new Intent(this, MapsMarkerView.class);
		 	    startActivity(intent_mpmrkrs);
				break;
		}
		
	}
	
	protected void onRestart() {
		Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		super.onRestart();
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

}
