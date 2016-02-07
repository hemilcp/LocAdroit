package com.example.MultiUserPack;

import java.io.*;
import java.net.*;
import java.util.*;

import org.json.*;

import com.example.locationassistant.MainActivity;
import com.example.locationassistant.PickLocationActivity;
import com.example.locationassistant.R;

import com.example.BackgroundWorks.Constants;
import com.example.BackgroundWorks.RegisterApp;
import com.example.DBClasses.*;
import com.example.DBHelper.*;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

import android.os.AsyncTask;
import android.os.Bundle;
//import android.provider.ContactsContract.CommonDataKinds.*;
//import android.provider.ContactsContract.*;
import android.accounts.AccountManager;
import android.content.*;
//import android.database.Cursor;
import android.graphics.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
//import android.widget.AdapterView.OnItemSelectedListener;
import android.support.v7.app.ActionBarActivity;


public class AddMultiuserTaskActivity extends ActionBarActivity
{
	//private final List<SpinnerEntry> spinnerContent = new LinkedList<SpinnerEntry>();
	//private Spinner contactSpinner;
	//private final ContactsSpinnerAdapter adapter = new ContactsSpinnerAdapter(spinnerContent, this);
	ListViewEntry lve;
	TextView t;
	MyDBHelper dbhelper;
	PopupWindow popupWindowDogs;
	String locs[],newloc,date,email,loc_alias,title,note;
	ListView listViewDogs;
	int flag=0;
	TextView loc_new;
	AutoCompleteTextView emview;
	RadioGroup map_group;
	Button loc_change;
	double newlat,newlng;
	boolean dateflag=false,datechecked;
	String useremails[];
	GetUsersTask gtask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_multiuser_task);
		dbhelper = new MyDBHelper(this);
		
		if(!isRegistered())
		{
			Intent accpick=AccountPicker.newChooseAccountIntent(null,null,new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true,null,null,null,null);
			startActivityForResult(accpick,2);
			Toast.makeText(this, "Choose your account to register the application", Toast.LENGTH_LONG).show();
		}
		
		getEmails();
		popupWindowDogs = popupWindowDogs();
		/*contactSpinner = (Spinner)findViewById(R.id.contactsSpinner);
		
		contactSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				updateList(position);
			}

			public void onNothingSelected(AdapterView<?> parent) {
				updateList(contactSpinner.getSelectedItemPosition());
			};
			
			private void updateList(int position) {
				if(position < adapter.getCount() && position >= 0) {
					SpinnerEntry currentEntry = adapter.getItem(position);
					//queryAllPhoneNumbersForContact(currentEntry.getContactId(), content);
					lve = queryAllEmailAddressesForContact(currentEntry.getContactId());
					t = (TextView) findViewById(R.id.contact_alias);
					if(lve!=null) t.setText(lve.getDestinationAddress());
				}
			}
		});
		queryAllRawContacts();
		contactSpinner.setAdapter(adapter);*/
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
	    MenuInflater menuInflater = getMenuInflater();
	    menuInflater.inflate(R.menu.add_multiuser_task, menu);
	    return true;
	}
	
	protected class GetUsersTask extends AsyncTask<Void,Void,Void>
	{	@Override
		protected Void doInBackground(Void... params) 
		{	try
			{	Log.d("GetUsersTask","Trying to connect to server");
				URL url=new URL(Constants.userEmails);
				URLConnection con=url.openConnection();
				con.setDoOutput(true);
				HttpURLConnection hpcon=(HttpURLConnection)con;
				hpcon.connect();
				
				Log.d("Server","Going for Input");
				SharedPreferences pref=getSharedPreferences(Constants.PREF_FILE,Context.MODE_PRIVATE);
				int appid=pref.getInt("AppID",0);
				if(appid!=0)
				{	JSONObject jobj=new JSONObject();
					jobj.put("appid",appid);
					OutputStream out=hpcon.getOutputStream();
					out.write(jobj.toString().getBytes());
					out.close();
					Log.d("Connection Status",""+hpcon.getResponseCode());
					if(hpcon.getResponseCode()==200)
					{	InputStream is=hpcon.getInputStream();
						BufferedReader br=new BufferedReader(new InputStreamReader(is));
						String line,msg="";
						while((line=br.readLine())!=null)
						{	msg+=line;	}
						br.close();
						Log.d("Server-Message",msg);
						jobj=new JSONObject(msg);
						JSONArray jarr=jobj.getJSONArray("emails");
						useremails=new String[jarr.length()];
						for(int i=0;i<jarr.length();i++)
						{	useremails[i]=jarr.getString(i);	}
					}
				}
			}
			catch(Exception e)
			{	e.printStackTrace();	}
			return null;
		}
		@Override
		protected void onPostExecute(Void result)
		{	populateAdapter();		}
	}
	
	public void getEmails()
	{	
		if(Constants.isConnected(this))
		{	gtask=new GetUsersTask();
			gtask.execute();
		}
		else
		{	Toast.makeText(this, "Network Unavailable", Toast.LENGTH_LONG).show();	}
	}
	void populateAdapter()
	{
		emview=(AutoCompleteTextView)findViewById(R.id.selectUsers);
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,useremails);
		emview.setAdapter(adapter);
		emview.setThreshold(0);
	}
	public void onRadioButtonClicked(View view)
	{	 // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	    	case R.id.radio_favs:
	            if (checked)
	            {  	popupshow(view);	}
	            break;
	            
	        case R.id.radio_maps:
	            if (checked)
	            {    showMap(view);   }
	    }
	}
	
    public void popupshow(View view)
    {	
    	Vector<Location_Alias> locations=dbhelper.getLocations();
		if(locations==null)
		{	Toast.makeText(this,"No Locations Exist",Toast.LENGTH_SHORT).show();
			return;
		}
		else
		{	locs=new String[locations.size()];
			for(int i=0;i<locations.size();i++)
			{
				locs[i]=locations.get(i).getAlias();
			}
		}

        // set our adapter and pass our pop up window contents
        listViewDogs.setAdapter(dogsAdapter(locs));
      
        // set the item click listener
        listViewDogs.setOnItemClickListener(new DogsDropdownOnItemClickListener());
    	popupWindowDogs.showAtLocation(view,Gravity.AXIS_SPECIFIED,10,10);
    }
	public PopupWindow popupWindowDogs() 
	{	// initialize a pop up window type
        PopupWindow popupWindow = new PopupWindow(this);

        // the drop down list is a list view
        listViewDogs = new ListView(this);
      
        // some other visual settings
        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        //popupWindow.setHeight(400);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
      
        // set the list view as pop up window content
        popupWindow.setContentView(listViewDogs);

        return popupWindow;
	}
	 
	 private ArrayAdapter<String> dogsAdapter(String locs[]) 
	 {      ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, locs) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                // setting the ID and text for every items in the list
            	String text = getItem(position);              

            	// visual settings for the list item
	            TextView listItem = new TextView(AddMultiuserTaskActivity.this);
	
	            listItem.setText(text);
	            listItem.setTag(position);
	            listItem.setTextSize(22);
	            listItem.setPadding(10, 10, 10, 10);
	            listItem.setTextColor(Color.WHITE);
	            return listItem;
	        }
	 	};
	 	return adapter;
	 }
	
	 public class DogsDropdownOnItemClickListener implements OnItemClickListener 
	 {
	    @Override
	    public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
	        // get the text and set it as the button text
	    	//Toast.makeText(AddReminderActivity.this, "Selected Positon is: " + locs[arg2], 100).show();	       
	    	flag=0;
			popupWindowDogs.dismiss();
			
			map_group = (RadioGroup) findViewById(R.id.radio_group);
			map_group.setVisibility(View.GONE);
			
			loc_new = (TextView) findViewById(R.id.loc_alias);
			loc_new.setVisibility(View.VISIBLE);
			loc_new.setText(locs[arg2]);
			
			newloc = locs[arg2];
			
			loc_change = (Button) findViewById(R.id.loc_change);
			loc_change.setVisibility(View.VISIBLE);
			loc_change.setGravity(Gravity.RIGHT);
	    }
	}
	 
	 public void locChangeBut(View view)
	 {
		 loc_new.setText(null);
		 loc_new.setVisibility(View.GONE);
		 loc_change.setVisibility(View.GONE);
		 map_group.setVisibility(View.VISIBLE);
		 map_group.clearCheck();
	 }
	/*
private void queryAllRawContacts() {
		
		final String[] projection = new String[] {
				RawContacts.CONTACT_ID,					// the contact id column
				RawContacts.DELETED						// column if this contact is deleted
		};
		
		@SuppressWarnings("deprecation")
		final Cursor rawContacts = managedQuery(
				RawContacts.CONTENT_URI,				// the uri for raw contact provider
				projection,	
				null,									// selection = null, retrieve all entries
				null,									// not required because selection does not contain parameters
				null);									// do not order

		final int contactIdColumnIndex = rawContacts.getColumnIndex(RawContacts.CONTACT_ID);
		final int deletedColumnIndex = rawContacts.getColumnIndex(RawContacts.DELETED);
		
		spinnerContent.clear();
		if(rawContacts.moveToFirst()) {					// move the cursor to the first entry
			while(!rawContacts.isAfterLast()) {			// still a valid entry left?
				final int contactId = rawContacts.getInt(contactIdColumnIndex);
				final boolean deleted = (rawContacts.getInt(deletedColumnIndex) == 1);
				if(!deleted) {
					spinnerContent.add(queryDetailsForContactSpinnerEntry(contactId));
				}
				rawContacts.moveToNext();				// move to the next entry
			}
		}

		rawContacts.close();
	}

	@SuppressWarnings("deprecation")
	private SpinnerEntry queryDetailsForContactSpinnerEntry(int contactId) {
		final String[] projection = new String[] {
				Contacts.DISPLAY_NAME,					// the name of the contact
				Contacts.PHOTO_ID						// the id of the column in the data table for the image
		};

		final Cursor contact = managedQuery(
				Contacts.CONTENT_URI,
				projection,
				Contacts._ID + "=?",						// filter entries on the basis of the contact id
				new String[]{String.valueOf(contactId)},	// the parameter to which the contact id column is compared to
				null);
		
		if(contact.moveToFirst()) {
			final String name = contact.getString(
					contact.getColumnIndex(Contacts.DISPLAY_NAME));
			final String photoId = contact.getString(
					contact.getColumnIndex(Contacts.PHOTO_ID));
			final Bitmap photo;
			if(photoId != null) {
				photo = queryContactBitmap(photoId);
			} else {
				photo = null;
			}
			contact.close();
			return new SpinnerEntry(contactId, photo, name);
		}
		contact.close();
		return null;
	}

	@SuppressWarnings("deprecation")
	private Bitmap queryContactBitmap(String photoId) {
		final Cursor photo = managedQuery(
				Data.CONTENT_URI,
				new String[] {Photo.PHOTO},		// column where the blob is stored
				Data._ID + "=?",				// select row by id
				new String[]{photoId},			// filter by the given photoId
				null);
		
		final Bitmap photoBitmap;
		if(photo.moveToFirst()) {
			byte[] photoBlob = photo.getBlob(
					photo.getColumnIndex(Photo.PHOTO));
			photoBitmap = BitmapFactory.decodeByteArray(
					photoBlob, 0, photoBlob.length);
		} else {
			photoBitmap = null;
		}
		photo.close();
		return photoBitmap;
	}
	
	
	public ListViewEntry queryAllEmailAddressesForContact(int contactId) {
		final String[] projection = new String[] {
				Email.DATA,							// use Email.ADDRESS for API-Level 11+
				Email.TYPE
		};
		ListViewEntry lve = null;

		@SuppressWarnings("deprecation")
		final Cursor email = managedQuery(
				Email.CONTENT_URI,	
				projection,
				Data.CONTACT_ID + "=?",
				new String[]{String.valueOf(contactId)},
				null);

		if(email.moveToFirst()) {
			final int contactEmailColumnIndex = email.getColumnIndex(Email.DATA);
			final int contactTypeColumnIndex = email.getColumnIndex(Email.TYPE);
			
			if(!email.isAfterLast()) {
				final String address = email.getString(contactEmailColumnIndex);
				final int type = email.getInt(contactTypeColumnIndex);
				email.moveToNext();
				lve = new ListViewEntry(address, Email.getTypeLabelResource(type),R.string.type_email);
			}
			
		}
		email.close();
		return lve;
	}*/

	public void enterDetails(View v) 
	{
    	String t1,nt,loc="",dt,em;
    	t1= ((EditText) findViewById(R.id.editText1)).getText().toString();
    	nt = ((EditText) findViewById(R.id.editText2)).getText().toString();
    	if(flag==1)
    	{
    		Location_Alias loc1=new Location_Alias(newloc,newlat,newlng);
    		if(!dbhelper.addLocation(loc1))
    		{	Toast.makeText(this, "Location name must be unique", Toast.LENGTH_LONG).show(); }
    		else
    		{	loc = newloc;  		}
    	}
    	else
    	{	loc=newloc;    	}
    	 
    	//Spinner spn = (Spinner) findViewById(R.id.contactsSpinner);
    	EditText date=(EditText) findViewById(R.id.editTextDate);
    	dt = date.getText().toString();
    	em=emview.getText().toString();
    	//SpinnerEntry sen = (SpinnerEntry) spn.getSelectedItem();
    	//ListViewEntry lve = this.queryAllEmailAddressesForContact(sen.getContactId());
    	//em = lve.getDestinationAddress();
    	if(!(t1.equals("")) && !(nt.equals("")) && !(loc.equals(""))  && !dt.equals(""))
    	{	addToDB(t1,nt,loc,dt,em);	   	}
    	else
    	{	Toast.makeText(this,"Fill Up the Empty Fields", Toast.LENGTH_SHORT).show();	}
    }
	
	void addToDB(String t1,String nt,String loc,String dt,String em)
    {
		title=t1;	note=nt;	loc_alias=loc;	date=dt;	email=em;
		addEventToServer();
    }
	
	void addEventToServer()
	{
		ProgressBar pbar=(ProgressBar)findViewById(R.id.processing);
		pbar.setVisibility(View.VISIBLE);
		MultiUserEvent me=new MultiUserEvent(Constants.SENDER,title,note,loc_alias,date,email,Constants.GOT_EVENTID);
		SendEventToServer se=new SendEventToServer(this,me);
		try
		{	se.t.join();
			pbar.setVisibility(View.GONE);
			String resmsg=se.getResponseMsg();
			if(resmsg.equals("EVENT_NOT_ADDED"))
			{	Toast.makeText(this,"Event could not be added,due to communication error",Toast.LENGTH_LONG).show();	}
			else if(resmsg.equals("EVENT_ADDED"))
			{	Toast.makeText(this, "Event Successfully added", Toast.LENGTH_LONG).show();	}
			else
			{	Toast.makeText(this, "Network Unavailable,try again later", Toast.LENGTH_LONG).show();}
		}
		catch(Exception e)
		{	e.printStackTrace();	}
		
		Intent intent=new Intent(this,MainActivity.class);
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
	}
	
	boolean isRegistered()
	{
		SharedPreferences pref=getSharedPreferences(Constants.PREF_FILE,Context.MODE_PRIVATE);
		String reg=pref.getString("registrationId", null);
		if(reg==null || reg.equals(""))
		{	return false;	}
		else
		{	return true;	}
	}
	
	public void showDatePickerDialog(View v) {
    	android.support.v4.app.DialogFragment newFragment = new DatePickM();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
	
	public void showMap(View view)
	{
		Intent intent=new Intent(this,PickLocationActivity.class);
		startActivityForResult(intent,1);
	}
	@Override
	public void onActivityResult(int requestcode,int resultcode,Intent data)
	{
		
		if(requestcode==1)
		{	if(data==null)	{ flag=0;   return; }
			newloc  = data.getStringExtra("alias");
			newlat=data.getDoubleExtra("latitude",0);
			newlng=data.getDoubleExtra("longitude", 0);
			map_group = (RadioGroup) findViewById(R.id.radio_group);
			map_group.setVisibility(View.GONE);
			loc_new = (TextView) findViewById(R.id.loc_alias);
			loc_new.setVisibility(View.VISIBLE);
			loc_new.setText(newloc);
			loc_change = (Button) findViewById(R.id.loc_change);
			loc_change.setVisibility(View.VISIBLE);
			loc_change.setGravity(Gravity.RIGHT);
			flag=1;
		}
		else if(requestcode==2 && resultcode==RESULT_OK)
		{
			String accname = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			RegisterApp rapp=new RegisterApp(this,accname);
			addEventToServer();
		}
	}
}
