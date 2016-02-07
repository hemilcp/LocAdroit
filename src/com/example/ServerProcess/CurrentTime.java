import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

public class CurrentTime extends HttpServlet
{	public void doGet(HttpServletRequest request,HttpServletResponse response)throws IOException,ServletException
	{	System.out.println("Connected to Server");
	
		InputStream is=request.getInputStream();
		BufferedReader br=new BufferedReader(new InputStreamReader(is));
		String line,msg="";
		while((line=br.readLine())!=null)
		{	msg+=line;		}
		System.out.println("Message from client : "+msg);
		response.setStatus(200);
		Calendar cal=Calendar.getInstance();
		int dd,mm,yy;
		dd=cal.get(Calendar.DAY_OF_MONTH);
		mm=cal.get(Calendar.MONTH)+1;
		yy=cal.get(Calendar.YEAR);
		String day=(dd<10)?"0"+dd:""+dd;
		String mon=(mm<10)?"0"+mm:""+mm;
		String date=day+"-"+mon+"-"+yy;
		
		int hr,min,sec;
		hr=cal.get(Calendar.HOUR_OF_DAY);
		min=cal.get(Calendar.MINUTE);
		sec=cal.get(Calendar.SECOND);
		String hh=(hr<10)?"0"+hr:""+hr;
		String mn=(min<10)?"0"+min:""+min;
		String sc=(sec<10)?"0"+sec:""+sec;
		String time=hh+":"+mn+":"+sc;
		
		response.setContentType("text/plain");
		PrintWriter out=response.getWriter();
		out.println("SERVER DATE-TIME");
		out.println("DATE : "+date);
		out.println("TIME : "+time);
	}
	public void doPost(HttpServletRequest request,HttpServletResponse response)throws IOException,ServletException
	{	doGet(request,response);	}
}


/* SERVER CODE
public class MyTask extends AsyncTask<Void,Void,Void>
{

	@Override
	protected Void doInBackground(Void... params) 
	{	
		try
		{	Log.d("Connection Status","Trying to connect to server");
			URL url=new URL("http://192.168.1.3:8081/Servlet1");
			URLConnection con=url.openConnection();
			con.setDoOutput(true);
			HttpURLConnection hpcon=(HttpURLConnection)con;
			hpcon.connect();
			
			OutputStream os=hpcon.getOutputStream();
			PrintWriter out=new PrintWriter(new OutputStreamWriter(os));
			out.write("Hi Server");
			out.close();
			
			InputStream is=hpcon.getInputStream();
			BufferedReader br=new BufferedReader(new InputStreamReader(is));
			String line,msg="";
			while((line=br.readLine())!=null)
			{	msg+=line;	}
			br.close();
			Log.d("Server Message",""+msg);
			Log.d("Connection Status",""+hpcon.getResponseCode());
		}
		catch(Exception e)
		{	e.printStackTrace();	}
	
		return null;
	}
	
}*/