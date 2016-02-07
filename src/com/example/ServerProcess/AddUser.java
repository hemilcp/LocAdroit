import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class AddUser extends HttpServlet
{	DBEvents dbhelper;
	HttpIO hio;
	private static final long serialVersionUID=1;
	public void init()
	{	dbhelper=new DBEvents();	
		hio=new HttpIO();
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException
	{	doPost(request,response);
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException
	{	System.out.println("Add user event");
		String msg=hio.readInputString(request);
		JSONParser parser=new JSONParser();
		String email,regId="";
		try
		{	JSONObject jobj=(JSONObject)parser.parse(msg);
			email=(String)jobj.get("email");
			regId=(String)jobj.get("registrationId");
			int appid=dbhelper.addUser(email,regId);
			if(appid!=-1)
			{	response.setStatus(200);
				jobj=new JSONObject();
				jobj.put("appid",new Integer(appid));
				response.setContentType("application/json");
				PrintWriter out=response.getWriter();
				out.println(jobj.toString());
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