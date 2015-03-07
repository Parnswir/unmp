package com.parnswir.unmp.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import com.parnswir.unmp.R;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {
	
	public static final boolean DO_COMPRESS = true;
	public static final boolean DO_NOT_COMPRESS = false;
	
	private static final int STUB_ID = R.drawable.default_image;
	private static final int REQUIRED_SIZE = 64;
    
    private MemoryCache memoryCache = new MemoryCache();
    private Map<ImageView, Integer> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, Integer>());
    private ExecutorService executorService;
    private Handler handler = new Handler();
    private SQLiteDatabase DB;
    
    public ImageLoader(Context context, SQLiteDatabase db){
        executorService = Executors.newFixedThreadPool(1);
        DB = db;
    }
    
    public void displayAlbumImage(int ID, ImageView imageView, boolean compress, ImageRetriever handler)
    {
        imageViews.put(imageView, ID);
        Bitmap bitmap = memoryCache.get(Integer.toString(ID));
        if(bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            queuePhoto(ID, imageView, compress, handler);
        }
    }
        
    private void queuePhoto(int ID, ImageView imageView, boolean compress, ImageRetriever handler)
    {
        PhotoToLoad p = new PhotoToLoad(ID, imageView, compress, handler);
        executorService.submit(new PhotosLoader(p));
    }
    
    public static Bitmap decodeBitmap(byte[] b, boolean compress){
    	if (b == null)
    		return null;
    	
    	int scale = 1;
    	
    	if (compress) {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(b, 0, b.length, o);
			
			int width_tmp = o.outWidth, height_tmp=o.outHeight;
			while(true){
			    if(width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
			        break;
			    width_tmp /= 2;
			    height_tmp /= 2;
			    scale *= 2;
			}
    	}
		
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		return BitmapFactory.decodeByteArray(b, 0, b.length, o2);
    }
    
    private class PhotoToLoad
    {
        public int albumID;
        public ImageView imageView;
        public boolean doCompress;
        public ImageRetriever handler;
        
        public PhotoToLoad(int albumID, ImageView imageView, boolean compress, ImageRetriever handler){
            this.albumID = albumID;
            this.imageView = imageView;
            this.doCompress = compress;
            this.handler = handler;
        }
    }
    
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
        
        @Override
        public void run() {
            try{
                if(imageViewReused(photoToLoad))
                    return;
                Bitmap bmp = decodeBitmap(photoToLoad.handler.getBitmap(photoToLoad.albumID, DB), photoToLoad.doCompress);
                memoryCache.put(Integer.toString(photoToLoad.albumID), bmp);
                if(imageViewReused(photoToLoad))
                    return;
                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);
            }catch(Throwable th){
                th.printStackTrace();
            }
        }
    }
    
    boolean imageViewReused(PhotoToLoad photoToLoad){
        int tag = imageViews.get(photoToLoad.imageView);
        if(tag != photoToLoad.albumID)
            return true;
        return false;
    }
    
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad coverToLoad;
        
        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
        	bitmap = b; 
        	coverToLoad = p;
        }
        
        public void run()
        {
            if(imageViewReused(coverToLoad))
                return;
            if(bitmap != null)
                coverToLoad.imageView.setImageBitmap(bitmap);
            else
                coverToLoad.imageView.setImageResource(STUB_ID);
        }
    }

    public void clearCache() {
        memoryCache.clear();
    }

}
