import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class HttpIO 
{	public String readInputString(HttpServletRequest request)throws IOException
	{	InputStream is=request.getInputStream();
		BufferedReader br=new BufferedReader(new InputStreamReader(is));
		String line,msg="";
		while((line=br.readLine())!=null)
		{	msg+=line;		}
		br.close();
		return msg;
	}
}