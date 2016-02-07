public class Location
{	int locid;
	double latitude,longitude;
	String address;
	Location()
	{}
	Location(String addr,double lat,double lng)
	{	address=addr;	longitude=lng;	latitude=lat;	}
	void setId(int lid)
	{	locid=lid;	}
	int getId()
	{	return locid;	}
}