package com.example.DBClasses;

public class LogRecord
{	int lid,eid;
	String etype,date,estatus;
	public LogRecord(String type,int id,String status,String dt)
	{
		etype=type;
		eid=id;
		estatus=status;
		date=dt;
	}
	public void setId(int id)
	{	lid=id;	}
	public int getId()
	{	return lid;	}
	public String getEventType()
	{	return etype;	}
	public int getEventId()
	{	return eid;	}
	public String getStatus()
	{	return estatus;	}
	public String getDate()
	{	return date;	}
}
