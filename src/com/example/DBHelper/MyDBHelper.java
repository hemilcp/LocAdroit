package com.example.DBHelper;

import com.example.DBClasses.*;
import com.example.BackgroundWorks.*;

import java.util.*;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBHelper extends SQLiteOpenHelper{
	//ALL STATIC VARIABLES
	Context context;
	private static final int DB_VERSION=1;
	private static final String DB_NAME="LocationAssistantDB";
	private static final String TABLE_LOCATION="location_alias";
	private static final String COL_LOC_NAME="alias";
	private static final String COL_LAT="latitude";
	private static final String COL_LNG="longitude";
	
	private static final String TABLE_REMINDER="location_reminder";
	private static final String COL_RID="rid";
	private static final String COL_TITLE="title";
	private static final String COL_NOTE="note";
	private static final String COL_DATE="date";
	
	private static final String TABLE_SYSTASK="system_tasks";
	private static final String COL_SID="sid";
	private static final String COL_STYPE="systask_type";
	private static final String COL_SATTR="systask_attribute";
	private static final String COL_LCONDITION="location_condition";
	
	private static final String TABLE_MULTIUSER_TASK="multiuser_tasks";
	private static final String COL_MID="mid";
	private static final String COL_MTYPE="user_type";
	private static final String COL_EMAIL="user2email";
	private static final String COL_STATUS="status";
	
	private static final String TABLE_LOG="daily_log";
	private static final String COL_LID="lid";
	private static final String COL_EVENT="event_type";
	private static final String COL_EID="event_id";
	private static final String COL_ACTION="event_status";
	
	
	public MyDBHelper(Context context)
	{	super(context,DB_NAME,null,DB_VERSION);
		this.context=context;
	}
	
	//Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String CREATE_LOC_TABLE="create table "+TABLE_LOCATION+"("+COL_LOC_NAME+" TEXT PRIMARY KEY,"+COL_LAT+" REAL,"+COL_LNG+" REAL)";
		
		String CREATE_LOC_REMINDER="create table "+TABLE_REMINDER+"("+COL_RID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
				+COL_TITLE+" TEXT,"+COL_NOTE+" TEXT,"+COL_LOC_NAME+" TEXT,"+COL_DATE+" TEXT, " 
				+"CONSTRAINT FK_LID FOREIGN KEY(alias) REFERENCES "+TABLE_LOCATION+"("+COL_LOC_NAME+"))";
		
		String CREATE_SYSTASK="create table "+TABLE_SYSTASK+"("+COL_SID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"+COL_STYPE+
				" INTEGER,"+COL_SATTR+" TEXT,"+COL_LOC_NAME+" TEXT,"+COL_LCONDITION+" TEXT," +"CONSTRAINT FK_LNA FOREIGN KEY(alias) " +
						"REFERENCES "+TABLE_LOCATION+"("+COL_LOC_NAME+"))";
													
		String CREATE_MUSER="create table "+TABLE_MULTIUSER_TASK+"("+COL_MID+" INTEGER NOT NULL,"+COL_MTYPE+" INTEGER,"
						+COL_TITLE+" TEXT,"+COL_NOTE+" TEXT,"+COL_LOC_NAME+" TEXT,"+COL_DATE+" TEXT,"+COL_EMAIL+" TEXT," +
						COL_STATUS+" INTEGER,"+"CONSTRAINT FK_LID FOREIGN KEY(alias) REFERENCES "+TABLE_MULTIUSER_TASK+"("+COL_LOC_NAME+"))";
		
		String CREATE_LOG_TABLE="create table "+TABLE_LOG+"("+COL_LID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"+
		COL_EVENT+" TEXT,"+COL_EID+" INTEGER,"+COL_ACTION+" TEXT,"+COL_DATE+" TEXT)";
		
		db.execSQL(CREATE_LOC_TABLE);
		db.execSQL(CREATE_LOC_REMINDER);
		db.execSQL(CREATE_SYSTASK);
		db.execSQL(CREATE_MUSER);
		db.execSQL(CREATE_LOG_TABLE);
	}
	
	//Upgrading Tables
	@Override
	public void onUpgrade(SQLiteDatabase db,int oldversion,int newversion)
	{
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_REMINDER);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_SYSTASK);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_MULTIUSER_TASK);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_LOCATION);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_LOG);
		onCreate(db);
	}
	
	//Adding new Location  
	public boolean addLocation(Location_Alias lc)
	{
		Location_Alias loc=getLocation(lc.getAlias());
		if(loc==null)
		{	SQLiteDatabase db=this.getWritableDatabase();
			ContentValues values=new ContentValues();
			try
			{
				values.put(COL_LOC_NAME,lc.getAlias());
				values.put(COL_LAT, lc.getLat());
				values.put(COL_LNG, lc.getLng());
				db.insert(TABLE_LOCATION, null, values);
				goBroadcast(Constants.LOC_ADDED);
			}
			catch(SQLiteException se)
			{	db.close();
				return false;  }
			finally
			{	db.close();		}
			return true;
		}
		else
		{	goBroadcast(Constants.LOC_ADDED);
			return true;	
		}
		
	}


	//Adding new reminder
	//Don't have to insert id field,as it is autoincrement feature
	public void addReminder(LocationReminder rm)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put(COL_TITLE,rm.getTitle());
		values.put(COL_NOTE,rm.getNote());
		values.put(COL_LOC_NAME, rm.getLoc());
		values.put(COL_DATE, rm.getDate());
		db.insert(TABLE_REMINDER, null, values);
		goBroadcast(Constants.EVENT_REMINDER);
		db.close();
	}
	
	public LocationReminder getLastReminder()
	{
		SQLiteDatabase db=this.getReadableDatabase();
		Cursor cursor=db.rawQuery("select * from "+TABLE_REMINDER+" where rid=(select max(rid) from "+TABLE_REMINDER+")",null);
		LocationReminder loc=null;
		if(cursor.moveToFirst())
		{	loc=new LocationReminder(cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4));
			loc.setId(cursor.getInt(0));		
			cursor.close();
			db.close();
		}
		cursor.close();
		db.close();
		return loc;
	}
	
	//Getting Reminders List
	public List<LocationReminder> getReminders()
	{
		SQLiteDatabase db=this.getReadableDatabase();
		Cursor cursor=db.rawQuery("select * from "+TABLE_REMINDER,null);
		List<LocationReminder> locs=new ArrayList<LocationReminder>();
		if(cursor.moveToFirst())
		{	
			do
			{	LocationReminder lc=new LocationReminder(cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4));
				lc.setId(cursor.getInt(0));
				locs.add(lc);
			}while(cursor.moveToNext());			
			cursor.close();
			db.close();
		}
		else
		{	locs=null;		}
		cursor.close();
		db.close();
		return locs;
	}
	
	public void deleteReminder(int id)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		db.delete(TABLE_REMINDER,""+COL_RID+"="+id+"", null);
		db.close();
	}
	
	//Getting a particular record
	public Location_Alias getLocation(String lname)
	{
		SQLiteDatabase db=this.getReadableDatabase();
		String qString="select * from "+TABLE_LOCATION+" WHERE UPPER("+COL_LOC_NAME+")=UPPER('"+lname+"')";
		Cursor cursor=db.rawQuery(qString, null);
		Location_Alias lc=null;
		if(cursor.moveToFirst())
		{	lc=new Location_Alias(cursor.getString(0),cursor.getDouble(1),cursor.getDouble(2));		}
		cursor.close();
		db.close();
		return lc;
	}
	
	//delete location
	public int deleteloc(String loc)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		db.delete(TABLE_REMINDER, "UPPER("+COL_LOC_NAME+")=UPPER('"+loc+"')", null);
		db.delete(TABLE_SYSTASK, "UPPER("+COL_LOC_NAME+")=UPPER('"+loc+"')",null);
		db.delete(TABLE_MULTIUSER_TASK, "UPPER("+COL_LOC_NAME+")=UPPER('"+loc+"')",null);
		int r = db.delete(TABLE_LOCATION, "UPPER("+COL_LOC_NAME+")=UPPER('"+loc+"')", null);
		db.close();
		goBroadcast(Constants.DELETION);
		return r;
	}
	
	//Getting all records
	public Vector<Location_Alias> getLocations()
	{
		SQLiteDatabase db=this.getReadableDatabase();
		Cursor cursor=db.rawQuery("select * from "+TABLE_LOCATION,null);
		Vector<Location_Alias> locs=null;
		if(cursor.moveToFirst())
		{	locs=new Vector<Location_Alias>();
			do
			{	Location_Alias lc=new Location_Alias(cursor.getString(0),cursor.getDouble(1),cursor.getDouble(2));
				locs.add(lc);
			}while(cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return locs;
	}
	public ArrayList<LocationReminder> getMatchedReminders(String loc,String date)
	{
		String queryString="select * from "+TABLE_REMINDER+" where upper("+COL_LOC_NAME+")=upper('"+loc+"') " +
				"and ("+COL_DATE+"='"+date+"' or "+COL_DATE+" is null)";
		SQLiteDatabase db=this.getReadableDatabase();
		Cursor cursor=db.rawQuery(queryString,null);
		ArrayList<LocationReminder> locrems=new ArrayList<LocationReminder>();
		LocationReminder reminder;
		if(cursor.moveToFirst())
		{
			do
			{
				reminder=new LocationReminder(cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4));
				reminder.setId(cursor.getInt(0));
				locrems.add(reminder);
			}while(cursor.moveToNext());
		}
		else
		{	locrems=null;	}
		cursor.close();
		db.close();
		return locrems;
	}
	
	
	public void addLogRecord(LogRecord ld)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put(COL_EVENT,ld.getEventType());
		values.put(COL_EID,ld.getEventId());
		values.put(COL_ACTION, ld.getStatus());
		values.put(COL_DATE, ld.getDate());
		db.insert(TABLE_LOG, null, values);
		db.close();
		Log.d("LogRecordAdded",ld.getEventType()+" : "+ld.getEventId());
	}
	
	public int getLastLog()
	{
		int id=-1;
		SQLiteDatabase db=this.getReadableDatabase();
		Cursor cursor=db.rawQuery("select max("+COL_LID+") from "+TABLE_LOG,null);
		if(cursor.moveToFirst())
		{	id=cursor.getInt(0);	}
		cursor.close();
		db.close();
		return id;
	}
	
	public ArrayList<LogRecord> getTodaysLog(String date,String etype)
	{
		SQLiteDatabase db=this.getReadableDatabase();
		Cursor cursor=db.rawQuery("select * from "+TABLE_LOG+" where "+COL_DATE+"='"+date+"' and upper("+COL_EVENT+")=upper('"+etype+"')",null);
		ArrayList<LogRecord> logrecords=new ArrayList<LogRecord>();
		LogRecord lrecord=null;
		if(cursor.moveToFirst())
		{
			do
			{	lrecord=new LogRecord(cursor.getString(1),cursor.getInt(2),cursor.getString(3),cursor.getString(4));
				lrecord.setId(cursor.getInt(0));
				logrecords.add(lrecord);
			}while(cursor.moveToNext());
		}
		else
		{	logrecords=null;	}
		cursor.close();
		db.close();
		return logrecords;
	}
	public void changeLogStatus(int logid,String status)
	{
		 SQLiteDatabase db = this.getWritableDatabase();
		 ContentValues values = new ContentValues();
		 values.put(COL_ACTION, status);
		 db.update(TABLE_LOG, values, COL_LID+"="+logid,null);
		 db.close();
		 Log.d("MyDBHelper","Changing log status\nID : "+logid+"\nSTATUS : "+status);
	}
	
	public void changeSnoozeStatus(String date,String location)
	{
		SQLiteDatabase dbr = this.getReadableDatabase();
		Cursor cursor=dbr.rawQuery("select rid from "+TABLE_REMINDER+" where upper("+COL_LOC_NAME+")=upper('"+location+"')",null);
		LocationReminder reminder;
		if(cursor.moveToFirst())
		{
			do
			{ 
				reminder=new LocationReminder(cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4));
				reminder.setId(cursor.getInt(0));
				changeSnooze(date,reminder);
			}while(cursor.moveToNext());
		}
		cursor.close();	
		dbr.close();
		
	}
	void changeSnooze(String date,LocationReminder rem)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COL_ACTION, Constants.ACTION_NOT_DONE);
		db.update(TABLE_LOG, values, "upper("+COL_EVENT+")=upper('"+Constants.EVENT_REMINDER+"') and COL_EID="+rem.getId()+" and "+COL_DATE+"='"+date+"' and "+COL_ACTION+"='"+Constants.ACTION_SNOOZED+"'",null);
		db.close();
	}
	
	public void deleteLastDayTasks(String date)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		db.delete(TABLE_REMINDER,"upper("+COL_DATE+")=upper('"+date+"')", null);
		db.delete(TABLE_MULTIUSER_TASK,"upper("+COL_DATE+")=upper('"+date+"')", null);
		db.delete(TABLE_LOG,"upper("+COL_DATE+")=upper('"+date+"')", null);
		db.close();
	}
	
	public void addSysTask(SystemTasks sys)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		try{
			values.put(COL_STYPE,sys.getType());
			values.put(COL_SATTR,sys.getAttr());
			values.put(COL_LOC_NAME,sys.getLoc());
			values.put(COL_LCONDITION,sys.getCond());
			db.insert(TABLE_SYSTASK,null,values);
		}
		catch(SQLiteException se)
		{}
		goBroadcast(Constants.EVENT_SYSTASK);
		db.close();
	}
	
	public List<SystemTasks> getSysTasks()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cr = db.rawQuery("select * from "+TABLE_SYSTASK, null);
		List<SystemTasks> syslist = new ArrayList<SystemTasks>();
		if(cr.moveToFirst())
		{
			do{
				SystemTasks systasks = new SystemTasks(cr.getInt(1),cr.getString(2),cr.getString(3),cr.getString(4));
				systasks.setId(cr.getInt(0));
				syslist.add(systasks);
			}while(cr.moveToNext());
			cr.close();
			db.close();
			return syslist;
		}
		else
		{
			cr.close();
			db.close();
			return null;
		}
	}
	
	public SystemTasks getLastSTask()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cr = db.rawQuery("select * from "+TABLE_SYSTASK+" where sid=(select max(sid) from "+TABLE_SYSTASK+")", null);
		SystemTasks systasks = null;
		if(cr.moveToFirst())
		{	systasks = new SystemTasks(cr.getInt(1),cr.getString(2),cr.getString(3),cr.getString(4));
			systasks.setId(cr.getInt(0));
		}
		cr.close();
		db.close();
		return systasks;
	}
	
	public ArrayList<SystemTasks> getMatchedTasks(String loc,String when)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cr = db.rawQuery("select * from "+TABLE_SYSTASK+" where upper("+COL_LOC_NAME+")=upper('"+loc+"')" +
				" and upper("+COL_LCONDITION+")=upper('"+when+"')", null);
		ArrayList<SystemTasks> syslist = new ArrayList<SystemTasks>();
		if(cr.moveToFirst())
		{
			do{
				SystemTasks systasks = new SystemTasks(cr.getInt(1),cr.getString(2),cr.getString(3),cr.getString(4));
				systasks.setId(cr.getInt(0));
				syslist.add(systasks);
			}while(cr.moveToNext());
			
		}
		else
		{	syslist=null;		}
		cr.close();
		db.close();
		return syslist;
	}
	
	//Delete a System Task
	public void deleteSysTask(int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_SYSTASK,""+ COL_SID+"="+id, null);
		goBroadcast(Constants.DELETION);
		db.close();
	}
	
	public void addMultiUserEvent(MultiUserEvent me)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		try{
			values.put(COL_MID, me.getId());
			values.put(COL_MTYPE,me.getType());
			values.put(COL_TITLE,me.getTitle());
			values.put(COL_NOTE,me.getNote());
			values.put(COL_LOC_NAME,me.getLoc());
			values.put(COL_DATE, me.getDate());
			values.put(COL_EMAIL,me.getEmail());			
			values.put(COL_STATUS, Constants.GOT_EVENTID);
			db.insert(TABLE_MULTIUSER_TASK,null,values);
		}
		catch(SQLiteException se)
		{	se.printStackTrace();	}
		db.close();
		if(me.getType()==Constants.RECEIVER)
		{	goBroadcast(Constants.EVENT_USER);	}
	}
	
	
	public ArrayList<MultiUserEvent> getPendingMTasks(String date)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cr = db.rawQuery("select * from "+TABLE_MULTIUSER_TASK+" where upper("+COL_DATE+")=upper('"+date+"') and (("+COL_STATUS+"="+Constants.GOT_EVENTID+") or ("+COL_STATUS+"="+Constants.EVENT_PENDING+")) ", null);
		MultiUserEvent me = null;
		ArrayList<MultiUserEvent> mtasks=new ArrayList<MultiUserEvent>();
		if(cr.moveToFirst())
		{	do
			{	me = new MultiUserEvent(cr.getInt(1),cr.getString(2),cr.getString(3),cr.getString(4),cr.getString(5),cr.getString(6),cr.getInt(7));
				me.setId(cr.getInt(0));
				mtasks.add(me);
			}while(cr.moveToNext());
		}
		cr.close();
		db.close();
		return mtasks;
	}
	
	public MultiUserEvent getMEventData(int eid)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cr = db.rawQuery("select * from "+TABLE_MULTIUSER_TASK+" where "+COL_MID+"="+eid, null);
		MultiUserEvent me = null;
		if(cr.moveToFirst())
		{	me = new MultiUserEvent(cr.getInt(1),cr.getString(2),cr.getString(3),cr.getString(4),cr.getString(5),cr.getString(6),cr.getInt(7));
			me.setId(cr.getInt(0));
		}
		cr.close();
		db.close();
		return me;
	}
	
	public MultiUserEvent getLastMEvent()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cr = db.rawQuery("select * from "+TABLE_MULTIUSER_TASK+" where "+COL_MID+"=(select max(mid) from "+TABLE_MULTIUSER_TASK+" where "+COL_MTYPE+"="+Constants.RECEIVER+")", null);
		MultiUserEvent me = null;
		if(cr.moveToFirst())
		{	me = new MultiUserEvent(cr.getInt(1),cr.getString(2),cr.getString(3),cr.getString(4),cr.getString(5),cr.getString(6),cr.getInt(7));
			me.setId(cr.getInt(0));
		}
		cr.close();
		db.close();
		return me;
	}
	
	public ArrayList<MultiUserEvent> getMatchedMTasks(String loc,String date)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cr = db.rawQuery("select * from "+TABLE_MULTIUSER_TASK+" where "+COL_MTYPE+"="+Constants.RECEIVER+" and upper("+COL_DATE+")=upper('"+date+"') and upper("+COL_LOC_NAME+")=upper('"+loc+"')", null);
		MultiUserEvent me = null;
		ArrayList<MultiUserEvent> mtasks=new ArrayList<MultiUserEvent>();
		if(cr.moveToFirst())
		{	do
			{	me = new MultiUserEvent(cr.getInt(1),cr.getString(2),cr.getString(3),cr.getString(4),cr.getString(5),cr.getString(6),cr.getInt(7));
				me.setId(cr.getInt(0));
				mtasks.add(me);
			}while(cr.moveToNext());
		}
		cr.close();
		db.close();
		return mtasks;
	}
	public ArrayList<MultiUserEvent> getAllMuserTasks()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cr = db.rawQuery("select * from "+TABLE_MULTIUSER_TASK+" where "+COL_MTYPE+"="+Constants.SENDER, null);
		MultiUserEvent me = null;
		ArrayList<MultiUserEvent> mtasks=new ArrayList<MultiUserEvent>();
		if(cr.moveToFirst())
		{	do
			{	me = new MultiUserEvent(cr.getInt(1),cr.getString(2),cr.getString(3),cr.getString(4),cr.getString(5),cr.getString(6),cr.getInt(7));
				me.setId(cr.getInt(0));
				mtasks.add(me);
			}while(cr.moveToNext());
		}
		cr.close();
		db.close();
		return mtasks;
	}
	public void updateStatus(int eid,int stat)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COL_STATUS, stat);
		db.update(TABLE_MULTIUSER_TASK, values,""+COL_MID+"="+eid,null);
		db.close();
	}
	
	public void deleteMTask(int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_MULTIUSER_TASK,""+ COL_MID+"="+id, null);
		db.close();
	}
	
	public void goBroadcast(String table)
	{
		Intent intent=new Intent();
		intent.setAction(Constants.DB_MODIFIED);
		intent.putExtra("Table",table);
		context.sendBroadcast(intent);
	}
}
