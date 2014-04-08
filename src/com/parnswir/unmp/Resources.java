package com.parnswir.unmp;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class Resources {
	
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

}
