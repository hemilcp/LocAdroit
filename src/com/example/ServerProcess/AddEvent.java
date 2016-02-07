import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class AddEvent extends HttpServlet
{	DBEvents dbhelper;
	HttpIO hio;
	public void init()
	{	dbhelper=new DBEvents();	
		hio=new HttpIO();
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException
	{	doPost(request,response);	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException
	{	System.out.println("\n\nAdd multi-user event");
		String msg=hio.readInputString(request);
		System.out.println("\nFrom client :"+msg);
		JSONParser parser=new JSONParser();
		JSONObject jobj;
		try
		{	jobj=(JSONObject)parser.parse(msg);	
			
			String locaddress=(String)jobj.get("address");
			double lat=(Double)jobj.get("latitude");
			double lng=(Double)jobj.get("longitude");
			Location loc=new Location(locaddress,lat,lng);
			int locid=dbhelper.addLocation(loc);
			
			MultiUserEvent me=new MultiUserEvent();
			me.locid=locid;
			me.user1=(int)((long)jobj.get("user1"));
			String email=(String)jobj.get("email");
			me.user2=dbhelper.checkUser(email);
			me.date=(String)jobj.get("date");
			me.status=1;
			int eid=dbhelper.addEvent(me);
			if(eid!=-1)
			{	response.setStatus(200);	
				jobj=new JSONObject();
				jobj.put("eventid",eid);
				PrintWriter out=response.getWriter();
				out.write(jobj.toString());
				
				//Send this event details to other user using gcmclient
				GCMClient gcm=new GCMClient();
				gcm.sendEventData(eid);
			}	
			else
			{	response.setStatus(204);	}
		}
		catch(Exception e)
		{	e.printStackTrace();
			response.setStatus(204);
		}
	}
}