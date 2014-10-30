package com.parnswir.unmp.core;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;


public abstract class NotificationBuilder {
	
	public static NotificationBuilder getInstance(Context context) {
		final int sdkVersion = Build.VERSION.SDK_INT;
		NotificationBuilder builder = null;
		if (sdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
			builder = IceCreamSandwichNotificationBuilder.getInstance(context);
		} else {
			builder = JellybeanNotificationBuilder.getInstance(context);
		}
		return builder;
	}
	
	public abstract Notification build();
	
	public abstract NotificationBuilder setSmallIcon(int resource);
	public abstract NotificationBuilder setLargeIcon(Bitmap bitmap);
	public abstract NotificationBuilder setOngoing(boolean value);
	public abstract NotificationBuilder setContentIntent(PendingIntent pendingIntent);
	public abstract NotificationBuilder setContentTitle(String title);
	public abstract NotificationBuilder setContentText(String text);
	public abstract NotificationBuilder addAction(int icon, String text, PendingIntent pendingIntent);
	
}
