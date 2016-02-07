package com.example.MultiUserPack;

import com.example.locationassistant.R;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ContactListViewAdapter {
	private final List<ListViewEntry> content;
	private final Activity activity;

	public ContactListViewAdapter(List<ListViewEntry> content, Activity activity) {
		this.content = content;
		this.activity = activity;
	}

	public int getCount() {
		return content.size();
	}

	public ListViewEntry getItem(int position) {
		return content.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView,
			ViewGroup parent) {
		final LayoutInflater inflater = activity.getLayoutInflater();	// default layout inflater
		final View listEntry = inflater.inflate(
				R.layout.listview_two_line_entry, null);				// initialize the layout from xml
		final TextView number = (TextView) listEntry
				.findViewById(R.id.numberPhoneNumber);
		final TextView type = (TextView) listEntry
				.findViewById(R.id.numberType);
		final ListViewEntry current = content.get(position);
		number.setText(current.getDestinationAddress());
		type.setText(activity.getString(current
				.getEntryLabelResource())
				+ ": "
				+ activity.getString(current.getTypeResource()));
		return listEntry;
	}
}