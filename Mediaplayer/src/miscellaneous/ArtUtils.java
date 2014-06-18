package miscellaneous;

import java.io.FileDescriptor;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import com.somethin.mediaplayer.R;

import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

public class ArtUtils {
	
	private static final HashMap<Long, Bitmap> albumArtCache = new HashMap<Long, Bitmap>();
	
	public static Bitmap getCachedAlbumArt(long albumID, Context context){
		
		if(albumArtCache.containsKey(albumID))
			return albumArtCache.get(albumID);
		
		//BitmapDrawable bmd = new BitmapDrawable(context.getResources(),getAlbumArt(albumID, context));
		Bitmap bmd = getBitmapAlbumArt(albumID, context,256,256);
		albumArtCache.put(albumID, bmd);
		return bmd;
			
	}
	
	/*public static Bitmap getAlbumArt(long albumID, Context context){
		         Bitmap bm = null;
		         try 
		         {
		             final Uri sArtworkUri = Uri
		                 .parse("content://media/external/audio/albumart");

		             Uri uri = ContentUris.withAppendedId(sArtworkUri, albumID);

		             ParcelFileDescriptor pfd = context.getContentResolver()
		                 .openFileDescriptor(uri, "r");

		             if (pfd != null) 
		             {
		                 FileDescriptor fd = pfd.getFileDescriptor();
		                 bm = BitmapFactory.decodeFileDescriptor(fd);
		             }
		         	 } catch (Exception e) {
		         	 }
		         return bm;
		        
	}*/
	
	public static Bitmap getBitmapAlbumArt(long albumID, Context context, int reqWidth, int reqHeight){
        Bitmap bm = null;
        try 
        {
            final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, albumID);

            ParcelFileDescriptor pfd = context.getContentResolver()
                .openFileDescriptor(uri, "r");

            if (pfd != null) 
            {
                FileDescriptor fd = pfd.getFileDescriptor();
             return decodeSampledBitmap(fd, 0, reqWidth, reqHeight);
            }
        	 } catch (Exception e) {
        	 }
        return 	BitmapFactory.decodeResource(context.getResources(),R.drawable.generic_music_file);
        //return new BitmapDrawable(context.getResources(), bm);
}
	
	public static Bitmap decodeSampledBitmap(FileDescriptor fd, int resId,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFileDescriptor(fd, null, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFileDescriptor(fd, null, options);
	}
	
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
				final int height = options.outHeight;
				final int width = options.outWidth;
				int inSampleSize = 1;

				if (height > reqHeight || width > reqWidth) {

					final int halfHeight = height / 2;
					final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
					while ((halfHeight / inSampleSize) > reqHeight
							&& (halfWidth / inSampleSize) > reqWidth) {
						inSampleSize *= 2;
					}
				}
				
				return inSampleSize;
	}

	public static class BitmapLoaderTask extends AsyncTask<Object, Void, Bitmap> {
		
		public static void loadBitmap(long resId, ImageView imageView, Context context) {
		    if (AsyncDrawable.cancelPotentialWork(resId, imageView)) {
		        final BitmapLoaderTask task = new BitmapLoaderTask(imageView);
		        final AsyncDrawable asyncDrawable =
		                new AsyncDrawable(context.getResources(),null/*BitmapFactory.decodeResource(context.getResources(),R.drawable.generic_music_file)*/, task);
		        imageView.setImageDrawable(asyncDrawable);
		        
		        task.execute(resId, context);
	}
	}
		private final WeakReference<ImageView> imageViewReference;
		int data = 0;
	    public BitmapLoaderTask(ImageView imageView) {
	        // Use a WeakReference to ensure the ImageView can be garbage collected
	        imageViewReference = new WeakReference<ImageView>(imageView);
	    }

	    // Once complete, see if ImageView is still around and set bitmap.
	    @Override
	    protected void onPostExecute(Bitmap bitmap) {
	        if (isCancelled()) {
	            bitmap = null;
	        }

	        if (imageViewReference != null && bitmap != null) {
	            final ImageView imageView = imageViewReference.get();
	            final BitmapLoaderTask bitmapWorkerTask =
	            		AsyncDrawable.getBitmapLoaderTask(imageView);
	            if (this == bitmapWorkerTask && imageView != null) {
	                imageView.setImageBitmap(bitmap);
	             /*   AlphaAnimation aa = new AlphaAnimation(0f,1f);
		        aa.setDuration(500);
		        imageView.startAnimation(aa);*/
	            }
	        }
	    }
		@Override
		protected Bitmap doInBackground(Object... params) {
			
			long albumID= (long) params[0];
			Context context= (Context) params[1];
			//int reqWidth = (int) params[2];
		//	int reqHeight = (int) params[3];
			return getCachedAlbumArt( albumID,  context);
			//return ArtUtils.getBitmapAlbumArt( albumID,  context,  reqWidth,  reqHeight);
		}

		
	
	}
		
	public static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<BitmapLoaderTask> bitmapWorkerTaskReference;
		public AsyncDrawable(Resources res, Bitmap bitmap, BitmapLoaderTask bitmapLoaderTask) {
			super(res, bitmap);
			bitmapWorkerTaskReference = new WeakReference<BitmapLoaderTask>(bitmapLoaderTask);
		}
		
		public BitmapLoaderTask getBitmapLoaderTask() {
		        return bitmapWorkerTaskReference.get();
		}
		public static boolean cancelPotentialWork(long data, ImageView imageView) {
			final BitmapLoaderTask bitmapLoaderTask = getBitmapLoaderTask(imageView);
			if (bitmapLoaderTask != null) {
			final int bitmapData = bitmapLoaderTask.data;
			// If bitmapData is not yet set or it differs from the new data
	        if (bitmapData == 0 || bitmapData != data) {		            // Cancel previous task
	        	bitmapLoaderTask.cancel(true);
	        } else {
	        	// The same work is already in progress
	        	return false;
	        }
			}
			    // No task associated with the ImageView, or an existing task was cancelled
			return true;
		}
		private static BitmapLoaderTask getBitmapLoaderTask(ImageView imageView) {
			   if (imageView != null) {
			       final Drawable drawable = imageView.getDrawable();
			       if (drawable instanceof AsyncDrawable) {
			           final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
			           return asyncDrawable.getBitmapLoaderTask();
			       }
			    }
			    return null;
			}
	}
		
	
	


	
}