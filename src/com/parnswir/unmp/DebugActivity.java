package com.parnswir.unmp;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import android.database.Cursor;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parnswir.unmp.core.CoverList;
import com.parnswir.unmp.media.FileCrawlerThread;
import com.parnswir.unmp.playlist.Playlist;
import com.parnswir.unmp.playlist.parser.WPLParser;
import com.parnswir.unmp.playlist.parser.WPLParser.WPLParserException;

public class DebugActivity extends DrawerActivity implements Observer {
	
	private ArrayAdapter<String> adapter;
	private FileCrawlerThread crawler;
	
	CoverList items = new CoverList();
	
	@Override
	protected void onStart() {
		super.onStart();
		DrawerState bums = getState();
		if (bums == null) {
			super.onDestroy();
		}
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
