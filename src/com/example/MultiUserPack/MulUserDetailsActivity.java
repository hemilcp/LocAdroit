package com.example.MultiUserPack;

import com.example.locationassistant.*;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class MulUserDetailsActivity extends ActionBarActivity 
{

	MulUserFragment mul = new MulUserFragment();
	String type;
	TextView umail,title,detail,loc,date;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mul_userdetails);
		umail = (TextView) findViewById(R.id.enduser_in);
		title = (TextView) findViewById(R.id.title_in);
		detail = (TextView) findViewById(R.id.note_in);
		loc = (TextView) findViewById(R.id.loc_in);
		date = (TextView) findViewById(R.id.day_in);
		
		umail.setText(getIntent().getStringExtra("Email"));
		title.setText(getIntent().getStringExtra("Title"));
		detail.setText(getIntent().getStringExtra("Note"));
		loc.setText(getIntent().getStringExtra("Location"));
		date.setText(getIntent().getStringExtra("Date"));
	}
}
