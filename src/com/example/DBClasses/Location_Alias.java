package com.example.DBClasses;

public class Location_Alias 
{	String loc_alias;
	double longitude,latitude;
	public Location_Alias()
	{	}
	public Location_Alias(String a,double lat,double lng)
	{
		loc_alias=a;	latitude=lat;	longitude=lng;
	}
	public String getAlias()
	{	return loc_alias;	}
	public double getLat()
	{	return latitude;	}
	public double getLng()
	{	return longitude;	}
}
