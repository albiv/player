package com.somethin.mediaplayer;

import miscellaneous.MusicUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender.SendIntentException;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class PlaylistDialogFragment extends DialogFragment {

	static PlaylistDialogFragment newInstance(long collectionId, int collectionType) {
		PlaylistDialogFragment f = new PlaylistDialogFragment();

		Bundle args = new Bundle();
		args.putLong("collectionId", collectionId);
		args.putInt("collectionType", collectionType);
		f.setArguments(args);

		return f;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final long collectionId = getArguments().getLong("collectionId");
		final int collectionType = getArguments().getInt("collectionType");
		final Cursor cursor = MusicUtils.getPlaylists(getActivity());
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.choose_playlist);

		builder.setCursor(cursor, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {
				cursor.moveToPosition(id);
				long playlistId = cursor.getLong(cursor.getColumnIndex("_ID"));
				MusicUtils.addTracksToRealPlaylist(playlistId, collectionId,
						getActivity(), collectionType);
				cursor.close();
				dialog.dismiss();
			}
																					}, "NAME");

		builder.setNeutralButton(R.string.create_playlist,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								getActivity());
						final EditText input = new EditText(getActivity());
						builder.setView(input);
						final Context context = getActivity();
						builder.setPositiveButton("ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										String name = input.getText()
												.toString();
										MusicUtils.buildRealPlaylist(name,
												collectionId, context, collectionType);
										dialog.dismiss();

									}
								});
						builder.show();

					}
				});
		return builder.create();
	}

}
