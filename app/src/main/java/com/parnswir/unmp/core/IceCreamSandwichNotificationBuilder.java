package com.parnswir.unmp.core;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

public class IceCreamSandwichNotificationBuilder extends NotificationBuilder {

	Builder builder;
	
	public static IceCreamSandwichNotificationBuilder getInstance(Context context) {
		IceCreamSandwichNotificationBuilder instance = new IceCreamSandwichNotificationBuilder();
		instance.builder = new NotificationCompat.Builder(context);
		return instance;
	}
	
	@Override
	public Notification build() {
		return builder.build();
	}

	@Override
	public IceCreamSandwichNotificationBuilder setSmallIcon(int resource) {
		builder.setSmallIcon(resource);
		return this;
	}

	@Override
	public IceCreamSandwichNotificationBuilder setLargeIcon(Bitmap bitmap) {
		return this;
	}

	@Override
	public IceCreamSandwichNotificationBuilder setOngoing(boolean value) {
		builder.setOngoing(value);
		return this;
	}

	@Override
	public IceCreamSandwichNotificationBuilder setContentIntent(PendingIntent pendingIntent) {
		builder.setContentIntent(pendingIntent);
		return this;
	}

	@Override
	public IceCreamSandwichNotificationBuilder setContentTitle(String title) {
		builder.setContentTitle(title);
		return this;
	}

	@Override
	public IceCreamSandwichNotificationBuilder setContentText(String text) {
		builder.setContentText(text);
		return this;
	}

	@Override
	public IceCreamSandwichNotificationBuilder addAction(int icon, String text, PendingIntent pendingIntent) {
		builder.addAction(icon, text, pendingIntent);
		return this;
	}

}
