package com.parnswir.unmp.core;

import com.parnswir.unmp.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

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
		public int layout = R.layout.list_fragment;
		
		public FragmentProperties(String aTitle, int aLayout) {
			title = aTitle;
			layout = aLayout;
		}
		
		public FragmentProperties(String aTitle) {
			title = aTitle;
		}
	}

}
