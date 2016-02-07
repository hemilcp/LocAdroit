import java.sql.*;
import java.io.*;
import java.util.*;

public class DBEvents
{	DBEvents()
	{	}
	public Connection getConnection()
	{	Connection connection=null;
		try
		{	Properties props=new Properties();
			InputStream input=new FileInputStream("E:\\SOFTWAREs\\apache-tomcat-8.0.5\\webapps\\ROOT\\WEB-INF\\classes\\Database.props");
			props.load(input);
			String driverstr=props.getProperty("driverstring");
			Class.forName(driverstr);
			//System.out.println("Connecting to the database...");  
			String url=props.getProperty("dbstring")+props.getProperty("ip_address")+":"+props.getProperty("port")+":"+props.getProperty("dbversion");
			connection = DriverManager.getConnection(url, props.getProperty("user"), props.getProperty("password"));  
		}
		catch(Exception e)
		{	e.printStackTrace();	
			connection=null;
		}
		return connection;
	}
	public int addUser(String email,String regid)
	{	Connection conn=getConnection();
		Statement stmt=null;
		ResultSet rs=null;
		int appid=-1;
		try
		{	stmt=conn.createStatement();
			String sql="insert into appusers(useremail,registrationid) values('"+email+"','"+regid+"')";
			stmt.executeUpdate(sql);
			stmt.close();
			
			//Getting APP-ID
			stmt=conn.createStatement();
			rs=stmt.executeQuery("select max(appid) from appusers");
			rs.next();
			appid=rs.getInt("max(appid)");
		}
		catch(Exception e)
		{	e.printStackTrace();
			appid=-1;
		}
		finally
		{	try
			{	if(rs!=null && stmt!=null && conn!=null)
				{	rs.close();	stmt.close();	conn.close();}
			}
			catch(Exception e){}
		}
		return appid;
	}
	public Vector<String> getRegistrationIds(String email)
	{	Vector<String> regIds=null;
		Connection conn=getConnection();
		Statement stmt=null;
		ResultSet rs=null;
		try
		{	stmt=conn.createStatement();
			rs=stmt.executeQuery("select * from appusers where upper(useremail) like upper('"+email+"')");
			if(rs.next())
			{	String regid;
				regIds=new Vector<String>();
				do
				{	regid=rs.getString("registrationid");	
					regIds.add(regid);
				}while(rs.next());
			}
		}
		catch(Exception e)
		{	e.printStackTrace();
			regIds=null;
		}
		finally
		{	try
			{	if(rs!=null && stmt!=null && conn!=null)
				{	rs.close();	stmt.close();	conn.close();}
			}
			catch(Exception e){}
		}
		return regIds;
	}
	public Vector<String> getUsers(int uid)
	{	Vector<String> emails=null;
		Connection conn=getConnection();
		Statement stmt=null;
		ResultSet rs=null;
		try
		{	stmt=conn.createStatement();
			rs=stmt.executeQuery("select * from appusers where appid!="+uid);
			if(rs.next())
			{	String em;
				emails=new Vector<String>();
				do
				{	em=rs.getString("useremail");
					emails.add(em);
				}while(rs.next());
			}
		}
		catch(Exception e)
		{	e.printStackTrace();
			emails=null;
		}
		finally
		{	try
			{	if(rs!=null && stmt!=null && conn!=null)
				{	rs.close();	stmt.close();	conn.close();}
			}
			catch(Exception e){}
		}
		return emails;
	}
	public int addLocation(Location loc)
	{	Connection conn=getConnection();
		Statement stmt=null;
		ResultSet rs=null;
		int locid=-1;
		try
		{	
			if(!alreadyExist(loc.address))
			{	stmt=conn.createStatement();
				String sql="insert into location(address,latitude,longitude) values('"+loc.address+"',"+loc.latitude+","+loc.longitude+")";
				stmt.executeUpdate(sql);
				stmt.close();
				
				stmt=conn.createStatement();
				rs=stmt.executeQuery("select max(locid) from location");
				rs.next();
				locid=rs.getInt("max(locid)");
			}
			else
			{	stmt=conn.createStatement();
				rs=stmt.executeQuery("select locid from location where upper(address) like upper('%"+loc.address+"%')");
				rs.next();
				locid=rs.getInt("locid");
			}
		}
		catch(Exception e)
		{	e.printStackTrace();
			locid=-1;
		}
		finally
		{	try
			{	if(rs!=null && stmt!=null && conn!=null)
				{	rs.close();	stmt.close();	conn.close();}
			}
			catch(Exception e){}
		}
		return locid;
	}
	
	public boolean alreadyExist(String addr)
	{
		Connection conn=getConnection();
		Statement stmt=null;
		ResultSet rs=null;
		boolean result=false;
		try
		{	stmt=conn.createStatement();
			rs=stmt.executeQuery("select * from location where upper(address) like upper('%"+addr+"%')");
			if(rs.next())
			{	result=true;	}
			else
			{	result=false;	}
		}
		catch(Exception e)
		{	e.printStackTrace();	result=false;	}
		finally
		{	try
			{	if(rs!=null && stmt!=null && conn!=null)
				{	rs.close();	stmt.close();	conn.close();}
			}
			catch(Exception e){}
		}
		return result;
	}
	public Location getLocation(int locid)
	{	Connection conn=getConnection();
		Statement stmt=null;
		ResultSet rs=null;
		Location loc=new Location();
		try
		{	stmt=conn.createStatement();
			rs=stmt.executeQuery("select * from location where locid="+locid);
			rs.next();
			loc.address=rs.getString("address");
			loc.longitude=rs.getDouble("longitude");
			loc.latitude=rs.getDouble("latitude");
		}
		catch(Exception e)
		{	e.printStackTrace();}
		finally
		{	try
			{	if(rs!=null && stmt!=null && conn!=null)
				{	rs.close();	stmt.close();	conn.close();}
			}
			catch(Exception e){}
		}
		return loc;
	}
	public int addEvent(MultiUserEvent me)
	{	Connection conn=getConnection();
		Statement stmt=null;
		ResultSet rs=null;
		int eventid=-1;
		try
		{	stmt=conn.createStatement();
			String sql="insert into multiuserevent(user1,user2,locid,status,eventdate) values("+me.user1+","+me.user2+","+me.locid+",1,'"+me.date+"')";
			stmt.executeUpdate(sql);
			stmt.close();
			
			stmt=conn.createStatement();
			rs=stmt.executeQuery("select max(eventid) from multiuserevent");
			rs.next();
			eventid=rs.getInt("max(eventid)");
		}
		catch(Exception e)
		{	e.printStackTrace();	
			eventid=-1;
		}
		finally
		{	try
			{	if(conn!=null)
				{ conn.close();}
			}
			catch(Exception e){}
		}
		return eventid;
	}
	public int checkUser(String email)
	{	Connection conn=getConnection();
		Statement stmt=null;
		ResultSet rs=null;
		int userid=-1;
		try
		{	stmt=conn.createStatement();
			rs=stmt.executeQuery("select * from appusers where upper(useremail) like upper('"+email+"')");
			if(rs.next())
			{	userid=rs.getInt("appid");	}
			else
			{	userid=-1;	}
		}
		catch(Exception e)
		{	e.printStackTrace();
			userid=-1;
		}
		finally
		{	try
			{	if(rs!=null && stmt!=null && conn!=null)
				{	rs.close();	stmt.close();	conn.close();}
			}
			catch(Exception e){}
		}
		return userid;
	}
	
	public String getEmail(int userid)
	{	Connection conn=getConnection();
		Statement stmt=null;
		ResultSet rs=null;
		String email="";
		try
		{	stmt=conn.createStatement();
			rs=stmt.executeQuery("select * from appusers where appid="+userid);
			rs.next();
			email=rs.getString("useremail");
		}
		catch(Exception e)
		{	e.printStackTrace();}
		finally
		{	try
			{	if(rs!=null && stmt!=null && conn!=null)
				{	rs.close();	stmt.close();	conn.close();}
			}
			catch(Exception e){}
		}
		return email;
	}
	public Vector<MultiUserEvent> getPendingEvents(int userid)
	{	Connection conn=getConnection();
		Statement stmt=null;
		ResultSet rs=null;
		Vector<MultiUserEvent> mevents=new Vector<MultiUserEvent>();
		try
		{	stmt=conn.createStatement();
			rs=stmt.executeQuery("select * from multiuserevent where user2="+userid+" and status=1");
			MultiUserEvent me;
			while(rs.next())
			{	me=new MultiUserEvent();
				me.eventid=rs.getInt("eventid");
				me.user1=rs.getInt("user1");
				me.user2=rs.getInt("user2");
				me.locid=rs.getInt("locid");
				me.status=rs.getInt("status");
				me.date=rs.getString("eventdate");
				mevents.add(me);
			}
		}
		catch(Exception e)
		{	e.printStackTrace();}
		finally
		{	try
			{	if(rs!=null && stmt!=null && conn!=null)
				{	rs.close();	stmt.close();	conn.close();}
			}
			catch(Exception e){}
		}
		return mevents;
	}
	
	public MultiUserEvent getEventDetails(int eid)
	{	MultiUserEvent me=null;
		Connection conn=getConnection();
		Statement stmt=null;
		ResultSet rs=null;
		Vector<MultiUserEvent> mevents=new Vector<MultiUserEvent>();
		try
		{	stmt=conn.createStatement();
			rs=stmt.executeQuery("select * from multiuserevent where eventid="+eid);
			while(rs.next())
			{	me=new MultiUserEvent();
				me.eventid=rs.getInt("eventid");
				me.user1=rs.getInt("user1");
				me.user2=rs.getInt("user2");
				me.locid=rs.getInt("locid");
				me.status=rs.getInt("status");
				me.date=rs.getString("eventdate");
			}
		}
		catch(Exception e)
		{	e.printStackTrace();}
		finally
		{	try
			{	if(rs!=null && stmt!=null && conn!=null)
				{	rs.close();	stmt.close();	conn.close();}
			}
			catch(Exception e){}
		}
		return me;
	}
	
	public void updateStatus(int eventid,int status)
	{	Connection conn=getConnection();
		Statement stmt=null;
		try
		{	stmt=conn.createStatement();
			String qstring="update multiuserevent set status="+status+" where eventid="+eventid;
			stmt.executeUpdate(qstring);
		}
		catch(Exception e)
		{	e.printStackTrace();	}
		finally
		{	try
			{	if(stmt!=null && conn!=null)
				{	stmt.close();	conn.close();}
			}
			catch(Exception e){}
		}
	}
}