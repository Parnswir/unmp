package com.parnswir.unmp.core;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.parnswir.unmp.PlayerFragment;

public class ProjectResources {
	
	public static class SquareLayout extends RelativeLayout {
		
		public SquareLayout(Context context) {
            super(context);
        }
        public SquareLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
        public SquareLayout(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }
		
		@Override
		public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		    super.onMeasure(widthMeasureSpec, widthMeasureSpec);
		}
	}
	
	public static class ProgressItem {
		public String text;
		public float count;
		public float value;
		
		public ProgressItem(String text, float value, float count) {
			this.text = text;
			this.value = value;
			this.count = count;
		}
	}
	
	public static class FragmentProperties {
		public String title;
		public Class<?> handler = PlayerFragment.class;
		public String tableName;
		
		public FragmentProperties(String title, Class<?> handler, String tableName) {
			super();
			init(title, handler, tableName);
		}
		
		public FragmentProperties(String title, Class<?> handler) {
			super();
			init(title, handler, "");
		}
		
		public FragmentProperties(String title) {
			super();
			init(title, PlayerFragment.class, "");
		}
		
		private void init(String aTitle, Class<?> aHandler, String aTableName) {
			title = aTitle;
			handler = aHandler;
			tableName = aTableName;
		}
	}

}
