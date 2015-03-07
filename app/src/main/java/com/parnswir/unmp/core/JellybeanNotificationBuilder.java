package com.parnswir.unmp.core;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

public class JellybeanNotificationBuilder extends NotificationBuilder {

	Builder builder;
	
	public static JellybeanNotificationBuilder getInstance(Context context) {
		JellybeanNotificationBuilder instance = new JellybeanNotificationBuilder();
		instance.builder = new Notification.Builder(context);
		return instance;
	}
	
	@Override
	public Notification build() {
		return builder.build();
	}

	@Override
	public JellybeanNotificationBuilder setSmallIcon(int resource) {
		builder.setSmallIcon(resource);
		return this;
	}

	@Override
	public JellybeanNotificationBuilder setLargeIcon(Bitmap bitmap) {
		builder.setLargeIcon(bitmap);
		return this;
	}

	@Override
	public JellybeanNotificationBuilder setOngoing(boolean value) {
		builder.setOngoing(value);
		return this;
	}

	@Override
	public JellybeanNotificationBuilder setContentIntent(PendingIntent pendingIntent) {
		builder.setContentIntent(pendingIntent);
		return this;
	}

	@Override
	public JellybeanNotificationBuilder setContentTitle(String title) {
		builder.setContentTitle(title);
		return this;
	}

	@Override
	public JellybeanNotificationBuilder setContentText(String text) {
		builder.setContentText(text);
		return this;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public JellybeanNotificationBuilder addAction(int icon, String text, PendingIntent pendingIntent) {
		builder.addAction(icon, text, pendingIntent);
		return this;
	}
	
}
