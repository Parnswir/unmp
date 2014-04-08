package com.parnswir.unmp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import com.parnswir.unmp.R;

public class PlayerService extends Service {
	
	public static final String EXTRA_ID = "state";
	public static final int STOP = 0;
	public static final int START = 1;

	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;

	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.arg2) {
				case STOP: stopSelf(msg.arg1); stopForeground(true); break;
				case START: setForeground(); break;
			}
		}
	}

	@Override
	public void onCreate() {
		HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_AUDIO);
		thread.start();

		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int action = intent.getExtras().getInt(EXTRA_ID);

		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		msg.arg2 = action;
		mServiceHandler.sendMessage(msg);

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		
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
}