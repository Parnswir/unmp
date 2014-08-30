package com.parnswir.unmp;

import java.io.File;
import java.io.IOException;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parnswir.unmp.core.C;
import com.parnswir.unmp.core.CoverList;
import com.parnswir.unmp.core.DatabaseUtils;
import com.parnswir.unmp.core.IconicAdapter;
import com.parnswir.unmp.media.FileCrawlerThread;
import com.parnswir.unmp.playlist.Playlist;
import com.parnswir.unmp.playlist.parser.WPLParser;
import com.parnswir.unmp.playlist.parser.WPLParser.WPLParserException;

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
		WPLParser p = new WPLParser(new File("/sdcard/Music/../Music/Playlists/sample2.wpl"), MainActivity.DB);
		Playlist l;
		try {
			l = p.buildPlaylist();
			Toast.makeText(getApplicationContext(), l.children.get(0).getCurrentFile(), Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WPLParserException e) {
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
	public void refresh(View view) {
		adapter.clear();
		items.clear();
		//Cursor c = DB.query("Images", null, C.COL_IMAGE_TYPE + " = \"" + C.ALBUM_ART_BLOB + "\"", null, null, null, null);
		//Cursor c = DB.query("ImageData", new String[] {"ID"}, null, null, null, null, "ID");
		//Cursor c = DB.query("Titles, TitlesAlbums, Albums, Images, ImageData", new String[] {"DISTINCT Albums.album, ImageData.image_blob"}, "Titles.ID = TitlesAlbums.title_id AND TitlesAlbums.album_id = Albums.ID AND Titles.ID = Images.ID AND Images.image_hash = ImageData.image_hash", null, null, null, "Albums.album");
		//Cursor c = DB.rawQuery("select distinct title, genre from Titles, TitlesGenres, Genres where Titles.ID=TitlesGenres.title_id AND Genres.ID=TitlesGenres.genre_id AND Genres.genre LIKE '(%)'", null);
		//Cursor c = DB.rawQuery("SELECT * FROM sqlite_master WHERE type='table';", null);
		Cursor c = MainActivity.DB.query(DatabaseUtils.getGiantJoin(), new String[] {C.TAB_TITLES + "." + C.COL_ID, C.COL_TITLE, C.COL_RATING}, C.TAB_TITLES + "." + C.COL_ID + " < 467", null, null, null, null);
		c.moveToFirst();
		while (! c.isAfterLast()) {
			String s = c.getString(1) + " // " + c.getString(2);
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
