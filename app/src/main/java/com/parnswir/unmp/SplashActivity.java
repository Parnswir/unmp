package com.parnswir.unmp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class SplashActivity extends Activity {
	
	public Handler handler;
		 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
      	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
      
      	setContentView(R.layout.activity_splash);
      
      	handler = new Handler();
 
      	IntentLauncher launcher = new IntentLauncher();
      	launcher.start();
	}
 
	private class IntentLauncher extends Thread {
      
		@Override
		public void run() {
         
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(SplashActivity.this, MainActivity.class);
					SplashActivity.this.startActivity(intent);
					SplashActivity.this.finish();
				}
			}, 1000);
		}
	}

}
