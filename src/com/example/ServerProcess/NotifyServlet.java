import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class NotifyServlet extends HttpServlet
{	DBEvents dbhelper;
	HttpIO hio;
	private static final long serialVersionUID=1;
	public void init()
	{	dbhelper=new DBEvents();	
		hio=new HttpIO();
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException
	{	doPost(request,response);	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException
	{	System.out.println("Notify other user event");
		String msg=hio.readInputString(request);
		JSONParser parser=new JSONParser();
		int eventid,answer;
		try
		{	JSONObject jobj=(JSONObject)parser.parse(msg);
			eventid=(int)((long)jobj.get("eventid"));
			dbhelper.updateStatus(eventid,4);
			response.setStatus(200);
			
			//Send this to other user
			GCMClient gcm=new GCMClient();
			gcm.notifyOtherUser(eventid);
		}
		catch(Exception e)
		{	e.printStackTrace();	
			response.setStatus(204);
		}
	}
}