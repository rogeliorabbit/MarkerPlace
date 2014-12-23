package com.hitglynorthz.markerplace;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hitglynorthz.markerplace.R;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.Toast;

public class MarkerSave extends ActionBarActivity implements OnClickListener {
	EditText name_edittext;
	EditText desc_edittext;
	EditText lat_edittext;
	EditText lng_edittext;
	private Button btnSave;
	private Button btn_img;
	private Button btn_galeria;
	private ImageView img_iv;
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_GALLERY = 2;
	private String imgPath;
	private String imgPatchDB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.add_marker);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

	    final String latString = getIntent().getStringExtra("latString");
	    final String lngString = getIntent().getStringExtra("lngString");
	    
	    img_iv = (ImageView) findViewById(R.id.img_iv);
	    lat_edittext = (EditText) findViewById(R.id.editText_lat);
	    lng_edittext = (EditText) findViewById(R.id.editText_lng);
	    lat_edittext.setFocusable(false);
	    lng_edittext.setFocusable(false);
	    
	    lat_edittext.setText(latString);
	    lng_edittext.setText(lngString);
	    
	    btn_img = (Button) findViewById(R.id.btn_img);
	    btn_img.setOnClickListener(this);
	    btn_galeria = (Button) findViewById(R.id.btn_galeria);
	    btn_galeria.setOnClickListener(this);
	    btnSave = (Button) findViewById(R.id.btn_save);
	    btnSave.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View view) {
	    	    name_edittext = (EditText) findViewById(R.id.editText_name);
	    	    desc_edittext = (EditText) findViewById(R.id.editText_desc);
	    	    String place_name = name_edittext.getText().toString();
	    	    String place_desc = desc_edittext.getText().toString();
	        	DBManager managerDB = new DBManager(MarkerSave.this);
	        	if(place_name.matches("") || place_desc.matches("")) {
		    		Toast.makeText(getBaseContext(), R.string.save_marker_error1, Toast.LENGTH_SHORT).show();
	        	}else{
		        	if(imgPatchDB == null) {
		        		imgPatchDB = getResources().getString(R.drawable.backdemo);
		        	}
		        	managerDB.insertMarker(place_name, place_desc, latString, lngString, imgPatchDB);
		    		Toast.makeText(getBaseContext(), getString(R.string.marker_save_done) + place_name, Toast.LENGTH_SHORT).show();
	         	   	Intent intent = new Intent(MarkerSave.this, MapsMarkerView.class);
	         	    startActivity(intent);
	         	   	finish();
	        	}
	    	}
	    });
	    
	}
	
	// Dependiendo del boton, se inicia la cámara o la galería para seleccionar una imágen
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.btn_img:
			try {
				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File f = createImageFile();
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
			    }
			}catch(IOException ex) {
	    		Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();	
			}
			break;
		case R.id.btn_galeria:
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.CONTENT_TYPE);
				startActivityForResult(Intent.createChooser(photoPickerIntent, "Seleccionar de: "), REQUEST_GALLERY);
			break;
		}
		
	}
	
	// Miramos la anchura de la pantalla y reescalamos la imagen para que encaje mejor al mostrarla
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			Display display = getWindowManager().getDefaultDisplay();
			int width = 400;
			if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
				width = display.getWidth();
			}else{
				Point size = new Point();
				getWindowManager().getDefaultDisplay().getSize(size);
				width = size.x;
			}
			if(requestCode == REQUEST_IMAGE_CAPTURE) {
				Bitmap resizedBitmap = decodeSampledBitmapFromFile(imgPath, width);
				imgPatchDB = imgPath;
				img_iv.setImageBitmap(resizedBitmap);
			}else if(requestCode == REQUEST_GALLERY){
				Uri selectedImage = data.getData();
				String rutaGaleria = getRealPathFromURI(selectedImage);
				imgPatchDB = rutaGaleria;
				Bitmap resizedBitmap = decodeSampledBitmapFromFile(rutaGaleria, width);
				img_iv.setImageBitmap(resizedBitmap);
			}
		}
	}
	
	// Creamos la carpeta para guardar las imagenes y les damos un nombre imgMPyyyyMMdd_HHmmss.jpg
	private File createImageFile() throws IOException {
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "imgMP" + timeStamp + ".jpg";
	    File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MarkerPhotos/");
	    dir.mkdirs();
	    File imageFile = new File(dir, imageFileName);
	    imgPath = imageFile.getAbsolutePath();
	    return imageFile;
	}
	
	// Calculamos el ancho y lo reescalamos
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth) {
	    final int width = options.outWidth;
	    int inSampleSize = +1;
	    if (width > reqWidth) {
	    	inSampleSize = Math.round((float) width / (float) reqWidth);
	    }
	    return inSampleSize;
	}
	
	// 
	public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth) {
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path, options);

	    options.inSampleSize = calculateInSampleSize(options, reqWidth);

	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(path, options);
	}
	
	
	// Buscamos el path de la imagen
	private String getRealPathFromURI(Uri contentURI) {
	    String result;
	    Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
	    if (cursor == null) {
	        result = contentURI.getPath();
	    } else { 
	        cursor.moveToFirst(); 
	        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
	        result = cursor.getString(idx);
	        cursor.close();
	    }
	    return result;
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
        	Intent intent = new Intent(this, SearchPlace.class);
        	startActivity(intent);
        	finish();
            return true;
        case R.id.action_settings:
    		Intent i = new Intent(this, Info.class);
    		startActivity(i);
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
