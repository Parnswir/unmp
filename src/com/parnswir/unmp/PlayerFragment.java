package com.parnswir.unmp;

import java.util.ArrayList;
import java.util.Locale;

import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parnswir.unmp.core.C;
import com.parnswir.unmp.core.DatabaseUtils;
import com.parnswir.unmp.core.ImageLoader;
import com.parnswir.unmp.media.MediaPlayerStatus;

public class PlayerFragment extends AbstractFragment {

	private static int LAB_POSITION = 0, LAB_LENGTH = 1, LAB_TITLE = 2, LAB_ARTIST = 3, LAB_ALBUM = 4;
	private static int BTN_REPEAT = 0, BTN_PREV = 1, BTN_PLAY = 2, BTN_NEXT = 3, BTN_SHUFFLE = 4;
	
	private ArrayList<ImageButton> playerControls = new ArrayList<ImageButton>();
	private ArrayList<TextView> playerLabels = new ArrayList<TextView>();
	private ProgressBar currentTitleProgress;
		
	private String currentTitle = "";
	private ImageLoader imageLoader;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	rootView = super.onCreateView(inflater, container, savedInstanceState);
    	
    	inflate(R.layout.activity_main);
    	showActionBar();
    	
    	imageLoader = new ImageLoader(activity, DB);
    	
    	setupPlayerControls();
    	updatePlayerStatus();
    	
        return rootView; 
    }
	
	
	public void setupPlayerControls() {
		playerControls.clear();
		int[] buttons = {R.id.btnRepeat, R.id.btnPrevious, R.id.btnPlay, R.id.btnNext, R.id.btnShuffle};
		for (int button : buttons) {
			playerControls.add((ImageButton) rootView.findViewById(button));
		}
		
		playerLabels.clear();
		int[] labels = {R.id.tvTime, R.id.tvTimeLeft, R.id.tvTitle, R.id.tvArtist, R.id.tvAlbum};
		for (int label : labels) {
			playerLabels.add((TextView) rootView.findViewById(label));
		}
		
		currentTitleProgress = (ProgressBar) rootView.findViewById(R.id.seekBar);
	}
	
	
	private MediaPlayerStatus getStatus() {
		return ((MainActivity) activity).playerStatus;
	}
	
	
	public void updatePlayerStatus() {
		setPlayIconTo(getStatus().paused || getStatus().stopped);	    		
		showTitleDuration();
		showCurrentPosition();
		updateTitleInfo();
	}
	
	
	private void setPlayIconTo(boolean shown) {
		Drawable icon;
		Resources res = getResources();
		if (shown) {
			icon = res.getDrawable(R.drawable.ic_action_play);
		} else {
			icon = res.getDrawable(R.drawable.ic_action_pause);
		}
		playerControls.get(BTN_PLAY).setImageDrawable(icon);
	}
	
	
	private void showCurrentPosition() {
		currentTitleProgress.setProgress(getStatus().position);
		playerLabels.get(LAB_POSITION).setText(formatPosition(getStatus().position));
	}
	
	
	private void showTitleDuration() {
		currentTitleProgress.setMax(getStatus().length);
		playerLabels.get(LAB_LENGTH).setText(formatPosition(getStatus().length));
	}
	
	
	private String formatPosition(int position) {
		int seconds = position / 1000;
		return String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60);
	}
	
	
	private void updateTitleInfo() {
		if (! getStatus().currentTitle.equals(currentTitle))
			setTitleInfo();	
	}
	
	
	private void setTitleInfo() {
		Cursor cursor = DB.query(DatabaseUtils.getGiantJoin(), new String[] {C.TAB_TITLES + "." + C.COL_ID, C.COL_TITLE, C.COL_ARTIST, C.COL_ALBUM, C.COL_YEAR}, C.COL_FILE + " = \"" + getStatus().currentTitle + "\"", null, null, null, null);
		cursor.moveToFirst();
		while (! cursor.isAfterLast()) {
			playerLabels.get(LAB_TITLE).setText(cursor.getString(1));
			playerLabels.get(LAB_ARTIST).setText(cursor.getString(2));
			playerLabels.get(LAB_ALBUM).setText(String.format(Locale.getDefault(), "%s [%s]", cursor.getString(3), cursor.getString(4)));
			setAlbumArt(cursor.getString(3));
			cursor.moveToNext();
		}
		cursor.close();
		currentTitle = getStatus().currentTitle;
	}
	
	
	private void setAlbumArt(String albumName) {
		ImageView view = (ImageView) rootView.findViewById(R.id.ivCover);
		imageLoader.displayAlbumImage(albumName, view, ImageLoader.DO_NOT_COMPRESS);
	}


	@Override
	public String getFragmentClass() {
		return "PlayerFragment";
	}
	
}
