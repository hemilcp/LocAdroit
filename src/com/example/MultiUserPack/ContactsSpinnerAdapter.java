package com.example.MultiUserPack;

import com.example.locationassistant.R;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

	public class ContactsSpinnerAdapter extends BaseAdapter implements
	SpinnerAdapter {
private final List<SpinnerEntry> content;
private final Activity activity;

public ContactsSpinnerAdapter(List<SpinnerEntry> content,
		Activity activity) {
	super();
	this.content = content;
	this.activity = activity;
}

public int getCount() {
	return content.size();
}

public SpinnerEntry getItem(int position) {
	return content.get(position);
}

public long getItemId(int position) {
	return position;
}

public View getView(int position, View convertView,
		ViewGroup parent) {
	final LayoutInflater inflater = activity.getLayoutInflater();	// default layout inflater
	final View spinnerEntry = inflater.inflate(R.layout.spinner_entry_with_icon, null);				// initialize the layout from xml
	final TextView contactName = (TextView) spinnerEntry.findViewById(R.id.spinnerEntryContactName);
	final ImageView contactImage = (ImageView) spinnerEntry.findViewById(R.id.spinnerEntryContactPhoto);
	final SpinnerEntry currentEntry = content.get(position);	
	contactName.setText(currentEntry.getContactName());
	contactImage.setImageBitmap(currentEntry.getContactPhoto());
	return spinnerEntry;
}
	}