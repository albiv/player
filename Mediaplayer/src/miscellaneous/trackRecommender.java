package miscellaneous;

import java.util.HashMap;
import java.util.List;

import android.graphics.Bitmap;

import com.echonest.api.v4.Artist;
import com.echonest.api.v4.Biography;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Params;

public class trackRecommender {

	 private static EchoNestAPI echoNest;
		private List<Artist> artists;
		private Artist artist; 
		private static final HashMap<String, Bitmap> artistImageCache = new HashMap<String, Bitmap>();
		
		public trackRecommender(){
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
		
}
