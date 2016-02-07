import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class CheckUsers extends HttpServlet
{	DBEvents dbhelper;
	HttpIO hio;
	public void init()
	{	dbhelper=new DBEvents();	
		hio=new HttpIO();
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException
	{	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException
	{	System.out.println("Check Users");
		String msg=hio.readInputString(request);
		JSONParser parser=new JSONParser();
		int uid=-1;
		try
		{	JSONObject jobj=(JSONObject)parser.parse(msg);	
			String email=(String)jobj.get("email");
			uid=dbhelper.checkUser(email);
			if(uid!=-1)
			{	response.setStatus(200);	}
			else
			{	response.setStatus(204);	}
		}
		catch(Exception e)
		{	response.setStatus(204);	}
	}
}