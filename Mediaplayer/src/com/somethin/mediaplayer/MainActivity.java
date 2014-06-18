package com.somethin.mediaplayer;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends ActionBarActivity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private String[] drawerSections;
	private ActionBarDrawerToggle mDrawerToggle;
	private  final int LIBRARY = 0;
	private  final int PLAYLISTS = 1;
	private  final int NEWS = 2;
	private  final int EXPLORE = 3;
	Fragment Libraryfragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null
				&& ((MyApp) getApplicationContext()).isPlaying()) {
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			// non funziona
			// ft.setCustomAnimations(R.anim.translatein_from_below,0);
			ft.add(R.id.playerFrag, new SmallPlayerFragment());
			ft.commit();
		}
		/*if(( Libraryfragment = getSupportFragmentManager().findFragmentByTag("cazzo"))!= null){
			getSupportFragmentManager().beginTransaction().remove(Libraryfragment).commit();
		}*/
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, new Main_Library_Fragment(),"cazzo").commit();
		setContentView(R.layout.activity_main);
		Libraryfragment = new Main_Library_Fragment();
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		drawerSections = getResources().getStringArray(
				R.array.drawer_strings_array);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, drawerSections));
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer,
				R.string.abc_searchview_description_clear,
				R.string.abc_searchview_description_query) {

			/** Called when a drawer has settled in a completely closed state. */
			@Override
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				// getActionBar().setTitle(mTitle);
				// invalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}

			/** Called when a drawer has settled in a completely open state. */
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				// getActionBar().setTitle(mDrawerTitle);
				// invalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// Called whenever we call invalidateOptionsMenu()

		/*
		 * @Override public boolean onPrepareOptionsMenu(Menu menu) { // If the
		 * nav drawer is open, hide action items related to the content view
		 * boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		 * menu.findItem(R.id.action_websearch).setVisible(!drawerOpen); return
		 * super.onPrepareOptionsMenu(menu); }
		 */

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		if (((MyApp) getApplicationContext()).isPlaying()) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.playerFrag, new SmallPlayerFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_search:
			return true;
		case R.id.action_random_play:
			return true;
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class DrawerItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	/** Swaps fragments in the main content view */
	private void selectItem(int position) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment = null;
		switch (position) {
		case LIBRARY:
			mDrawerLayout.closeDrawer(mDrawerList);
			fragment = new Main_Library_Fragment();
			fragmentManager.beginTransaction()
					.replace(R.id.container, fragment).commit();
			break;
		case PLAYLISTS:
			mDrawerLayout.closeDrawer(mDrawerList);
			fragment = new PlaylistsFragment();
			fragmentManager.beginTransaction()
					.replace(R.id.container, fragment).commit();
			break;

		case NEWS:
			mDrawerLayout.closeDrawer(mDrawerList);
			fragment = new NewsFragment();
			fragmentManager.beginTransaction()
					.replace(R.id.container, fragment).commit();
			break;

		case EXPLORE:
			mDrawerLayout.closeDrawer(mDrawerList);
			fragment = new ExploreFragment();
			fragmentManager.beginTransaction()
					.replace(R.id.container, fragment).commit();
			break;

		}
		// Insert the fragment by replacing any existing fragment

		// Highlight the selected item, update the title, and close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(drawerSections[position]);
		mDrawerLayout.closeDrawer(mDrawerList);

	}
}
