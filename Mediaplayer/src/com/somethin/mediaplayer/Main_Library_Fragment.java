package com.somethin.mediaplayer;

import com.astuetz.PagerSlidingTabStrip;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Main_Library_Fragment extends Fragment {
	private ViewPager mPager;
	private MyAdapter mAdapter;
	private PagerSlidingTabStrip tabs;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.viewpager_fragment, container,
				false);
		mAdapter = new MyAdapter(getFragmentManager());
		mPager = (ViewPager) view.findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
		tabs.setViewPager(mPager);
		tabs.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
			}
		});
		// Specify that the Home/Up button should not be enabled, since there is
		// no hierarchical
		// parent.
		return view;
	}

	public class MyAdapter extends FragmentStatePagerAdapter {
		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return LibraryArtistsFragment.newInstance(position);
			case 1:
				return LibraryAlbumsFragment.newInstance(position);
			case 2:
				return LibraryGenresFragment.newInstance(position);
			case 3:
				return LibraryTracksFragment.newInstance(position);
			}
			return null;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return new String(getString(R.string.artists));
			case 1:
				return new String(getString(R.string.albums));
			case 2:
				return new String(getString(R.string.genres));
			case 3:
				return new String(getString(R.string.tracks));
			}
			return null;
		}

	}
}
