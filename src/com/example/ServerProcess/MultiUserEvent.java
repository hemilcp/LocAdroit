public class MultiUserEvent
{	int eventid,user1,user2,locid,status;
	String date;
	MultiUserEvent()
	{	}
	MultiUserEvent(int u1,int u2,String d)
	{	user1=u1;	user2=u2;	date=d;	}
	void setID(int id)
	{	eventid=id;	}
	int getID()
	{	return eventid;	}
	void setStatus(int s)
	{	status=s;	}
	int setStatus()
	{	return status;	}
}