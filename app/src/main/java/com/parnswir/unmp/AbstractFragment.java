package com.parnswir.unmp;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parnswir.unmp.core.C;
import com.parnswir.unmp.core.DatabaseUtils;
import com.parnswir.unmp.playlist.MediaFile;
import com.parnswir.unmp.playlist.Playlist;

public abstract class AbstractFragment extends Fragment {
	
    protected DrawerActivity activity;
    protected View rootView;
    protected LayoutInflater inflater;
    protected ViewGroup container;
    protected SQLiteDatabase DB;
    protected SharedPreferences preferences;
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	activity = (DrawerActivity) getActivity();
    	this.inflater = inflater;
    	this.container = container;
    	
    	showTitle(C.FRAGMENTS[activity.selectedItem].title);
    	
    	DB = DatabaseUtils.getDB(activity);
    	preferences = activity.getPreferences(Context.MODE_PRIVATE);
    	
        return null; 
    }
    
    protected void inflate(int layoutID) {
    	rootView = inflater.inflate(layoutID, container, false);
    	activity.currentLayout = rootView;
    }
    
    protected void showActionBar() {
    	ActionBar actionBar = activity.getActionBar();
    	actionBar.show();
    }
    
    protected void showTitle(String title) {
    	ActionBar actionBar = activity.getActionBar();
    	actionBar.setTitle(title);
    }
    
    protected void playPlaylist(Playlist playlist, int position) {
    	if (playlist == null) return;
    	Bundle bundle = playlist.getBundled(PlayerService.FROM_PLAYLIST);
    	bundle.putInt(PlayerService.TIME, position);
    	PlayerService.setPlayerServiceState(activity, PlayerService.PLAY, bundle);
    	activity.selectItem(0);
    }
    
    protected void playFile(String fileName) {
		Playlist playlist = new Playlist();
		playlist.children.add(new MediaFile(fileName));
		playPlaylist(playlist, 0);
	}
    
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	return false;
    }
   
}
