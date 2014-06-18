package miscellaneous;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import com.echonest.api.v4.Artist;
import com.echonest.api.v4.Biography;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Params;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;


public class echonestTest {

	 private static EchoNestAPI echoNest;
	private List<Artist> artists;
	private Artist artist; 
	private static final HashMap<String, Bitmap> artistImageCache = new HashMap<String, Bitmap>();
	 public echonestTest() {
	        echoNest = new EchoNestAPI("VWQZ7LPIKPYCWUT0I");
	 }
	 
     public  List<Biography> getBiography(String name, int results) {
         Params p = new Params();
         p.add("name", name);
         p.add("results", results);

         List<Artist> artists = null;
		try {
			artists = echoNest.searchArtists(p);
		} catch (EchoNestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         List<Biography> biographies = null;
		try {
			biographies = artists.get(0).getBiographies();
		} catch (EchoNestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         return biographies;
     }
     
     public  Bitmap getArtistImage(String name) throws EchoNestException, IOException{
			artists = echoNest.searchArtists(name, 1);
			if ( artists.isEmpty()) return null;
			artist = artists.get(0);
			URL url;
			if (artist.getImages().isEmpty()) return null;
			url = new URL(artist.getImages().get(0).getURL());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    		 connection.connect();
			return BitmapFactory.decodeStream(connection.getInputStream());
		
     }
     
     class BitmapLoaderTask2 extends AsyncTask<String, Void, Bitmap> {
    	    private final WeakReference<ImageView> imageViewReference;
    	    private String data = null;

    	    public BitmapLoaderTask2(ImageView imageView) {
    	        // Use a WeakReference to ensure the ImageView can be garbage collected
    	        imageViewReference = new WeakReference<ImageView>(imageView);
    	    }

    	    // Decode image in background.
    	    @Override
    	    protected Bitmap doInBackground(String... params) {
    	        data = params[0];
    	        try {
    	        	if (artistImageCache.containsKey(data)) return artistImageCache.get(data);
    	        	Bitmap bitmap = getArtistImage(data);
					artistImageCache.put(data,bitmap);
					return bitmap;
				} catch (EchoNestException | IOException e) {
					return null;
				}
    	    }

    	    // Once complete, see if ImageView is still around and set bitmap.
    	    @Override
    	    protected void onPostExecute(Bitmap bitmap) {
    	    	 if (isCancelled()) {
    		            bitmap = null;
    		        }
    	        if (imageViewReference != null && bitmap != null) {
    	            final ImageView imageView = imageViewReference.get();
    	            if (imageView != null) {
    	                imageView.setImageBitmap(bitmap);
    	            }
    	        }
    	    }	
    	    
 	}
     public static  class AsyncDrawable2 extends BitmapDrawable {
			private final WeakReference<BitmapLoaderTask2> bitmapWorkerTaskReference;
			public AsyncDrawable2(Bitmap bitmap, BitmapLoaderTask2 bitmapWorkerTask) {
				super(bitmap);
				bitmapWorkerTaskReference = new WeakReference<BitmapLoaderTask2>(bitmapWorkerTask);
			}
			
			public BitmapLoaderTask2 getBitmapLoaderTask() {
			        return bitmapWorkerTaskReference.get();
			}
			public static  boolean cancelPotentialWork(String data, ImageView imageView) {
				final BitmapLoaderTask2 bitmapLoaderTask = getBitmapLoaderTask(imageView);
				if (bitmapLoaderTask != null) {
				final String bitmapData = bitmapLoaderTask.data;
				// If bitmapData is not yet set or it differs from the new data
		        if (bitmapData == null || bitmapData != data) {		            // Cancel previous task
		        	bitmapLoaderTask.cancel(true);
		        } else {
		        	// The same work is already in progress
		        	return false;
		        }
				}
				    // No task associated with the ImageView, or an existing task was cancelled
				return true;
			}
			private static BitmapLoaderTask2 getBitmapLoaderTask(ImageView imageView) {
				   if (imageView != null) {
				       final Drawable drawable = imageView.getDrawable();
				       if (drawable instanceof AsyncDrawable2) {
				           final AsyncDrawable2 asyncDrawable = (AsyncDrawable2) drawable;
				           return asyncDrawable.getBitmapLoaderTask();
				       }
				    }
				    return null;
				}
		}
     public void loadBitmap(String artistName, ImageView imageView) {
 	    if (AsyncDrawable2.cancelPotentialWork(artistName, imageView)) {
		        final BitmapLoaderTask2 task = new BitmapLoaderTask2(imageView);
		        final AsyncDrawable2 asyncDrawable =
		                new AsyncDrawable2(null /*BitmapFactory.decodeResource(context.getResources(),R.raw.white)*/, task);
		        imageView.setImageDrawable(asyncDrawable);
		        task.execute(artistName);
		        }
 	}
    
     
}
