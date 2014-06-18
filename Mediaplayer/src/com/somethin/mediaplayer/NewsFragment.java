package com.somethin.mediaplayer;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import miscellaneous.RSSFeedParser;
import miscellaneous.RSSFeedParser.Item;

import org.xmlpull.v1.XmlPullParserException;

import com.somethin.mediaplayer.adapters.NewsAdapter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebView.FindListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NewsFragment extends Fragment {
	private ListView listview;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.news_fragment, container, false);
		listview = (ListView) view.findViewById(R.id.newsList);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		URL url = null;
		try {
			url = new URL("http://www.youredm.com/feed/");
		} catch (MalformedURLException e2) {
			e2.printStackTrace();
		}

		new FUCKTASK().execute(url);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	private class FUCKTASK extends AsyncTask<URL, Void, List<Item>> {

		@Override
		protected List<Item> doInBackground(URL... params) {
			URL url = params[0];
			InputStream is = null;
			String BIGSTRING = "a";
			List<Item> list = null;

			HttpURLConnection conn = null;
			try {
				conn = (HttpURLConnection) url.openConnection();
			} catch (IOException e1) {
				Log.d("conn = (HttpURLConnection) url.openConnection();",
						"ssssssssssss");
				e1.printStackTrace();
			}
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			try {
				conn.setRequestMethod("GET");
			} catch (ProtocolException e1) {
				Log.d("ProtocolException", "ssssssssssss");
				e1.printStackTrace();
			}
			conn.setDoInput(true);
			// Starts the query
			try {
				conn.connect();
			} catch (IOException e1) {
				Log.d("conn.setDoInputException", "ssssssssssss");
				e1.printStackTrace();
			}
			try {
				is = conn.getInputStream();
			} catch (IOException e1) {
				Log.d("conn.getInputStream()", "ssssssssssss");

				e1.printStackTrace();
			}
			RSSFeedParser rss = new RSSFeedParser();
			try {
				list = rss.parse(is);

			} catch (XmlPullParserException | IOException e) {

				e.printStackTrace();
			}
			Log.d("PORCODIOOOOOOOOOOOO", Integer.toString(list.size()));
			//BIGSTRING = list.get(4).content;
			/*
			 * for (Item item : list) {
			 * 
			 * BIGSTRING = item.content;
			 * 
			 * 
			 * }
			 */
			
			return list;
		}

		@Override
		protected void onPostExecute(List<Item> result) {
			listview.setAdapter(new NewsAdapter(getActivity(), R.layout.news_listitem, result));
			//TextView textview = (TextView) getView().findViewById(R.id.text1);
			//textview.setText(Html.fromHtml(result));
			// textview.setMovementMethod(new ScrollingMovementMethod());
			//textview.setMovementMethod(new LinkMovementMethod());
			super.onPostExecute(result);

			
		}

	}

}
