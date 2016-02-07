package com.example.DBClasses;

public class SystemTasks {

	int id,type;
	String attr,location,loc_cond;

	public SystemTasks()
	{}
	
	public SystemTasks(int type,String attr,String loc,String cond)
	{
		this.type = type;
		this.attr = attr;
		location = loc;
		loc_cond = cond;
	}
	
	public void setId(int i)
	{	id = i;	}
	public int getId()
	{ return id; }
	public int getType()
	{  return type;	}
	public String getAttr()
	{  return attr;	}
	public String getLoc()
	{  return location;	}
	public String getCond()
	{  return loc_cond;  }
}
