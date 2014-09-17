package com.parnswir.unmp;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

import com.parnswir.unmp.core.C;
import com.parnswir.unmp.media.MediaPlayerStatus;
import com.parnswir.unmp.playlist.MediaFile;
import com.parnswir.unmp.playlist.Playlist;

public class PlayerService extends Service implements OnAudioFocusChangeListener {
	
	public static final String 
		EXTRA_ID = C.PREFIX + "state",
		EXTRA_STATUS = C.PREFIX + "status",
		STATUS_INTENT = C.PREFIX + "STATUS_INTENT",
		SERVICE_INTENT_BUNDLE = C.PREFIX + "intent_bundle",
		PLAYLIST_ADDITION = C.PREFIX + "addition",
		PLAYLIST_DELETION = C.PREFIX + "deletion",
		FILE_NAME = C.PREFIX + "file_name",
		FROM_PLAYLIST = C.PREFIX + "from_playlist";
	
	public static final int 
		STOP = 0,
		START = 1,
		PLAY = 2,
		PLAY_FILE = 3,
		PAUSE = 4,
		NEXT = 5,
		PREVIOUS = 6,
		
		NEW_PLAYLIST = 10,
		MODIFY_PLAYLIST = 11,
		
		STATUS = 255;

	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	private MediaPlayer player;
	private MediaPlayerStatus status = new MediaPlayerStatus();
	private Playlist playlist;
	
	private NoisyAudioStreamReceiver noisyAudioStreamReceiver = new NoisyAudioStreamReceiver();
	private boolean broadcastIsRegistered = false;
	
	private Timer secondsTimer = new Timer(true);
	
	
	public static void setPlayerServiceState(Context context, int state, Bundle bundle) {
		Intent intent = new Intent(context, PlayerService.class);
		intent.putExtra(EXTRA_ID, state);
		if (bundle == null) bundle = new Bundle();
		intent.putExtra(SERVICE_INTENT_BUNDLE, bundle);
		context.startService(intent);
	}
	

	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.arg2) {
				case STOP: stop(); stopSelf(msg.arg1); stopForeground(true); break;
				case START: setForeground(); break;
				case PLAY: handlePlayBundle(msg.getData()); startPlaylist(); break;
				case PAUSE: requestPause(); break;
				case NEXT: next(); break;
				case PREVIOUS: previous(); break;
				case STATUS: broadcastStatus(); break;
				case MODIFY_PLAYLIST: modifyPlaylist(msg.getData()); break;
			}	
		}
	}

	@Override
	public void onCreate() {
		HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_AUDIO);
		thread.start();

		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
		getAudioFocus();
		initializeMediaPlayer();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int action = intent.getExtras().getInt(EXTRA_ID);

		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		msg.arg2 = action;
		msg.setData(intent.getBundleExtra(SERVICE_INTENT_BUNDLE));
		mServiceHandler.sendMessage(msg);

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		if (player != null) player.release();
	}
	
	private void setForeground() {
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		
		Builder builder = new NotificationCompat.Builder(getApplicationContext())
			.setSmallIcon(R.drawable.ic_action_play)
			.setUsesChronometer(true)
			.setContentIntent(pendingIntent)
			.setContentTitle("Apotheosis")
			.setContentText("Greendjohn")
			.addAction(R.drawable.ic_action_play, "Play", pendingIntent)
			.addAction(R.drawable.ic_action_next, "Next", pendingIntent);
		Notification notification = builder.build();

		startForeground(1, notification);
	}
	
	private void getAudioFocus() {
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
		    AudioManager.AUDIOFOCUS_GAIN);

		if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
		    // could not get audio focus.
		}
	}
	
	private void initializeMediaPlayer() {
		player = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer arg0) {
				player.start();
				onResume();
			}
		});
		player.setOnErrorListener(new OnErrorListener() {	
			@Override
			public boolean onError(MediaPlayer mp, int error, int extra) {
				return false;
			}
		});
		player.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				next();
			}
		});
	}
	
	private void playFile(String filePath) {
		stop();
		setPlayerDataSource(filePath);
		preparePlayer();
	}
	
	private void startPlaylist() {
		playlist.reset();
		playCurrentFile();
	}
	
	private void handlePlayBundle(Bundle bundle) {
		if (bundle == null) return;
		Playlist source = (Playlist) bundle.getSerializable(FROM_PLAYLIST);
		if (source != null) {
			if (status.playing) stop();
			this.playlist = source;
		}
	}
	
	private void requestPause() {
		if (player.isPlaying()) {
			pause();
		} else if (status.paused) {
			play();
		}
	}
	
	private void play() {
		player.start();
		status.paused = false;
		onResume();
	}
	
	private void pause() {
		unregisterBroadcastReceiver();		
		player.pause();
		onPause();
	}
	
	private void stop() {
		unregisterBroadcastReceiver();
		//if (player.isPlaying()) player.stop();
		player.reset();
		onStop();
	}
	
	private void next() {
		if (status.playing || status.paused) {
			playlist.next();
			playCurrentFile();
		}
	}
	
	private void previous() {
		if (status.playing || status.paused) {
			playlist.previous();
			playCurrentFile();
		}
	}
	
	private void playCurrentFile() {
		if (!playlist.isFinished())
			playFile(playlist.getCurrentFile());
	}
	
	private void unregisterBroadcastReceiver() {
		if (broadcastIsRegistered) {
			unregisterReceiver(noisyAudioStreamReceiver);
			broadcastIsRegistered = false;
		}
	}
	
	private void registerBroadcastReceiver() {
		IntentFilter noiseFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
		registerReceiver(noisyAudioStreamReceiver, noiseFilter);
		broadcastIsRegistered = true;
	}
	
	private void startTimer() {
		secondsTimer = new Timer(true);
		secondsTimer.scheduleAtFixedRate(new BroadcastTask(), 0, 1000);
	}
	
	private void stopTimer() {
		secondsTimer.cancel();
		secondsTimer.purge();
	}
	
	private void onResume() {
		registerBroadcastReceiver();
		status.stopped = false;
	    broadcastStatus();
	    startTimer();
	}
	
	private void onStop() {
		status.stopped = true;
		broadcastStatus();
		stopTimer();
	}
	
	private void onPause() {
		status.paused = true;
		broadcastStatus();
		stopTimer();
	}
	
	private void setPlayerDataSource(String filePath) {
		status.currentTitle = filePath.replace("file://", "");
		try {
			player.setDataSource(filePath);
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private void preparePlayer() {
		try {
			player.prepareAsync();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private void modifyPlaylist(Bundle bundle) {
		String addition = bundle.getString(PLAYLIST_ADDITION);
		String deletion = bundle.getString(PLAYLIST_DELETION);
		
		if (addition != null) {
			playlist.children.add(new MediaFile(addition));
		} else {
			playlist.children.remove(new MediaFile(deletion));
		}
	}

	@Override
	public void onAudioFocusChange(int focusChange) {
		switch (focusChange) {
        case AudioManager.AUDIOFOCUS_GAIN: 
        	break;
        case AudioManager.AUDIOFOCUS_LOSS: 
        	break;
        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: 
        	// Lost focus for a short time, but we have to stop
            // playback. We don't release the media player because playback
            // is likely to resume
        	break;
        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
            // Lost focus for a short time, but it's ok to keep playing
            // at an attenuated level
            break;
		}
	}
	
	private void broadcastStatus() {
		Intent intent = new Intent(PlayerService.STATUS_INTENT);
		intent.putExtra(EXTRA_STATUS, getPlayerStatus());
		sendBroadcast(intent);	
	}
	
	private MediaPlayerStatus getPlayerStatus() {
		status.playing = player.isPlaying();
		if (status.playing || status.paused) {
			status.length = player.getDuration();
			status.position = player.getCurrentPosition();
		}
		return status;
	}
	
	private class NoisyAudioStreamReceiver extends BroadcastReceiver {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
	    		pause();
	      	}
	    }
	 }
	
	private class BroadcastTask extends TimerTask {
		@Override
		public void run() {
			broadcastStatus();			
		}		
	}
}