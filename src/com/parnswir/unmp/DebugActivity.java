package com.parnswir.unmp;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.parnswir.unmp.R;

public class DebugActivity extends Activity implements Observer {
	
	private ArrayAdapter<String> adapter;
	private FileCrawlerThread crawler;
	
	CoverList items = new CoverList();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        adapter = new IconicAdapter(this, items);
	    ((ListView) findViewById(R.id.lvMenu)).setAdapter(adapter);
	    
	    refresh(null);
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.debug, menu);
    	return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    public void searchFolder(View view) {
		EditText editText = (EditText) findViewById(R.id.edit_folder);
		String folder = editText.getText().toString(); 
		crawler = new FileCrawlerThread(MainActivity.DB, folder);
		crawler.callback.addObserver(this);
		crawler.start();
	}
	
	public void refresh(View view) {
		adapter.clear();
		items.clear();
		//Cursor c = DB.query("Images", null, C.COL_IMAGE_TYPE + " = \"" + C.ALBUM_ART_BLOB + "\"", null, null, null, null);
		//Cursor c = DB.query("ImageData", new String[] {"ID"}, null, null, null, null, "ID");
		//Cursor c = DB.query("Titles, TitlesAlbums, Albums, Images, ImageData", new String[] {"DISTINCT Albums.album, ImageData.image_blob"}, "Titles.ID = TitlesAlbums.title_id AND TitlesAlbums.album_id = Albums.ID AND Titles.ID = Images.ID AND Images.image_hash = ImageData.image_hash", null, null, null, "Albums.album");
		//Cursor c = DB.rawQuery("select distinct title, genre from Titles, TitlesGenres, Genres where Titles.ID=TitlesGenres.title_id AND Genres.ID=TitlesGenres.genre_id AND Genres.genre LIKE '(%)'", null);
		//Cursor c = DB.rawQuery("SELECT * FROM sqlite_master WHERE type='table';", null);
		Cursor c = MainActivity.DB.query("Albums", new String[] {"album"}, null, null, null, null, "album");
		c.moveToFirst();
		while (! c.isAfterLast()) {
			//items.images.add(DatabaseUtils.getAlbumArtFor(s, DB));
			String s = c.getString(0);
//			Cursor d = DB.query("ImageData", null, "ID = ?", new String[] {s}, null, null, null);
//			d.moveToFirst();
//			byte[] binaryData = d.getBlob(2);
//			items.images.add(binaryData);
			//d.close();
			items.images.add(null);
			items.names.add(s);
			c.moveToNext();
		} 
		c.close();
	}
	
	@Override
	public void update(Observable o, final Object arg) {		
		runOnUiThread(new Runnable() {
	        public void run()
	        {
	        	TextView tv = (TextView) findViewById(R.id.textview1);
	    		tv.setText((String) arg);
	        }
	    });
	}
}
