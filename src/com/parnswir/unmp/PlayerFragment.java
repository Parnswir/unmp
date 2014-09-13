package com.parnswir.unmp;

import java.util.ArrayList;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parnswir.unmp.core.C;
import com.parnswir.unmp.core.DatabaseUtils;
import com.parnswir.unmp.core.ImageLoader;
import com.parnswir.unmp.media.MediaPlayerStatus;
import com.parnswir.unmp.playlist.MediaFile;
import com.parnswir.unmp.playlist.Playlist;

public class PlayerFragment extends AbstractFragment {

	private static int LAB_POSITION = 0, LAB_LENGTH = 1, LAB_TITLE = 2, LAB_ARTIST = 3, LAB_ALBUM = 4;
	private static int BTN_REPEAT = 0, BTN_PREV = 1, BTN_PLAY = 2, BTN_NEXT = 3, BTN_SHUFFLE = 4;
	
	private ArrayList<ImageButton> playerControls = new ArrayList<ImageButton>();
	private ArrayList<TextView> playerLabels = new ArrayList<TextView>();
	private ProgressBar currentTitleProgress;
	private RatingBar ratingBar;
		
	private String currentTitle = "";
	private ImageLoader imageLoader;
	
	private MediaPlayerStatus playerStatus = new MediaPlayerStatus();
	private BroadcastReceiver statusBroadcastReceiver;
	private boolean receiving = false;
	
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
	
	
	@Override
	public void onStart() {
		super.onStart();
		setupIntentReceiver();
	}
	
	
	@Override
	public void onPause() {
		stopReceiving();
		if (playerStatus.playing) {
			PlayerService.setPlayerServiceState(activity, PlayerService.START, null);
		} else {
			PlayerService.setPlayerServiceState(activity, PlayerService.STOP, null);
		}
		super.onPause();
	}
	
	
	public void setupPlayerControls() {
		playerControls.clear();
		int[] buttons = {R.id.btnRepeat, R.id.btnPrevious, R.id.btnPlay, R.id.btnNext, R.id.btnShuffle};
		for (int button : buttons) {
			playerControls.add((ImageButton) rootView.findViewById(button));
		}
		
		playerControls.get(BTN_PLAY).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (playerStatus.stopped) {
					play();
				} else {
					pause();
				}
			}
		});
		
		playerControls.get(BTN_NEXT).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				next();
			}
		});
		
		playerLabels.clear();
		int[] labels = {R.id.tvTime, R.id.tvTimeLeft, R.id.tvTitle, R.id.tvArtist, R.id.tvAlbum};
		for (int label : labels) {
			playerLabels.add((TextView) rootView.findViewById(label));
		}
		
		currentTitleProgress = (ProgressBar) rootView.findViewById(R.id.seekBar);
		ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBar);
		ratingBar.setMax(10);
	}
	
	
	public void updatePlayerStatus() {
		setPlayIconTo(playerStatus.paused || playerStatus.stopped);	    		
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
		currentTitleProgress.setProgress(playerStatus.position);
		playerLabels.get(LAB_POSITION).setText(formatPosition(playerStatus.position));
	}
	
	
	private void showTitleDuration() {
		currentTitleProgress.setMax(playerStatus.length);
		playerLabels.get(LAB_LENGTH).setText(formatPosition(playerStatus.length));
	}
	
	
	private String formatPosition(int position) {
		int seconds = position / 1000;
		return String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60);
	}
	
	
	private void updateTitleInfo() {
		if (! playerStatus.currentTitle.equals(currentTitle))
			setTitleInfo();	
	}
	
	
	private void setTitleInfo() {
		Cursor cursor = DB.query(
				DatabaseUtils.getGiantJoin(), 
				new String[] {C.TAB_TITLES + "." + C.COL_ID, 
					C.COL_TITLE, C.COL_ARTIST, C.COL_ALBUM, 
					C.COL_YEAR, C.COL_RATING}, 
				C.COL_FILE + " = \"" + playerStatus.currentTitle + "\"", 
				null, null, null, null
		);
		cursor.moveToFirst();
		while (! cursor.isAfterLast()) {
			playerLabels.get(LAB_TITLE).setText(cursor.getString(1));
			playerLabels.get(LAB_ARTIST).setText(cursor.getString(2));
			playerLabels.get(LAB_ALBUM).setText(String.format(Locale.getDefault(), "%s [%s]", cursor.getString(3), cursor.getString(4)));
			setRating(cursor.getInt(5));
			setAlbumArt(cursor.getString(3));
			cursor.moveToNext();
		}
		cursor.close();
		currentTitle = playerStatus.currentTitle;
	}
	
	
	private void setRating(int rating) {
		ratingBar.setProgress(rating);
	}
	
	
	private void setAlbumArt(String albumName) {
		ImageView view = (ImageView) rootView.findViewById(R.id.ivCover);
		imageLoader.displayAlbumImage(albumName, view, ImageLoader.DO_NOT_COMPRESS);
	}


	@Override
	public String getFragmentClass() {
		return "PlayerFragment";
	}
	
	
	private void playFile(String fileName) {
		Playlist playlist = new Playlist();
		playlist.children.add(new MediaFile(fileName));
		PlayerService.setPlayerServiceState(activity, PlayerService.PLAY, playlist.getBundled(PlayerService.FROM_PLAYLIST));
	}
	
	
	private void play() {
		//playFile("file:///storage/sdcard0/Music/MIOIOIN/MOON EP/03 Hydrogen.mp3");
		
		Playlist playlist = new Playlist();
		playlist.children.add(new MediaFile("file:///storage/sdcard0/Music/MIOIOIN/MOON EP/03 Hydrogen.mp3"));
		playlist.children.add(new MediaFile("file:///storage/sdcard0/Music/Andreas Waldetoft/Europa Universalis III Soundtrack/04 Conquistador - Main Theme.mp3"));
		PlayerService.setPlayerServiceState(activity, PlayerService.PLAY, playlist.getBundled(PlayerService.FROM_PLAYLIST));
	}
	
	
	private void pause() {
		PlayerService.setPlayerServiceState(activity, PlayerService.PAUSE, null);
	}
	
	
	private void next() {
		PlayerService.setPlayerServiceState(activity, PlayerService.NEXT, null);
	}
	
	
	private void setupIntentReceiver() {
		if (!receiving) {
			receiving = true;
			statusBroadcastReceiver = new StatusIntentReceiver();
			IntentFilter statusFilter = new IntentFilter(PlayerService.STATUS_INTENT);
		    activity.registerReceiver(statusBroadcastReceiver, statusFilter);
		}		

	    PlayerService.setPlayerServiceState(activity, PlayerService.STATUS, null);
	}
	

	private void stopReceiving() {
		if (statusBroadcastReceiver != null && receiving) {
			activity.unregisterReceiver(statusBroadcastReceiver);
			receiving = false;
		}
	}
	
	
	private class StatusIntentReceiver extends BroadcastReceiver {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	if (PlayerService.STATUS_INTENT.equals(intent.getAction())) {
	    		playerStatus = (MediaPlayerStatus) intent.getSerializableExtra(PlayerService.EXTRA_STATUS);
	    	    updatePlayerStatus();
	      	}
	    }
		
	}
	
}
