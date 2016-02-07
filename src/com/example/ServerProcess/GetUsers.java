import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;


public class GetUsers extends HttpServlet
{	DBEvents dbhelper;
	HttpIO hio;
	public void init()
	{	dbhelper=new DBEvents();	
		hio=new HttpIO();
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException
	{	doPost(request,response);	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException
	{	System.out.println("Get Users");
		String msg=hio.readInputString(request);
		System.out.println("\nFrom client :"+msg);
		JSONParser parser=new JSONParser();
		JSONObject jobj;
		try
		{	jobj=(JSONObject)parser.parse(msg);	
			int appid=(int)((long)jobj.get("appid"));
			Vector<String> users=dbhelper.getUsers(appid);
			response.setStatus(200);	
			JSONArray jarray=new JSONArray();
			for(int i=0;i<users.size();i++)
			{	jarray.add(users.elementAt(i));	}
			jobj=new JSONObject();
			jobj.put("emails",jarray);
			PrintWriter out=response.getWriter();
			out.write(jobj.toString());
			out.close();
		}
		catch(Exception e)
		{	response.setStatus(204);	}
	}
}