import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.LinkedList;


public class HTTPconnection
{
	//private int DEFAULT_PORT = 80;
	//private String host = null;
	private Url currentURL;
	private String cmd;
	private LinkedList<String> cmdList;
	//private String GET = "GET / http/1.0\n";
	//private String POST = "POST / http/1.0\n";
	//private String HOST = null;
	private String SPACE = " ";
	private String BLANK_LINE = "\n";
	private Socket client = null;
	private InetSocketAddress sockAddr = null;
	
	private String sentContent;
	
	public HTTPconnection(Url currentWebsite)
	{
		this.currentURL = currentWebsite;
		cmdList = new LinkedList<String>();
	}

	public void setCmd(String command)
	{
		this.cmd = command;
		String httpCommand = cmd + SPACE + currentURL.getPath() + SPACE + currentURL.getProtocol() + BLANK_LINE;
		cmdList.add(httpCommand);
	}
	
	public void setPostContent(String sentContent)
	{
		//cmdList.add(sentContent);
		this.sentContent = sentContent;
	}
	
	public Page processURL()
	{
		String content = null;
		try
		{
			client = new Socket();
			sockAddr = new InetSocketAddress(currentURL.getHost(), currentURL.getPort());
			//String hostName = InetAddress.getLocalHost().getHostName();
			client.connect(sockAddr, 1000);
			Writer writer = new OutputStreamWriter(client.getOutputStream());
			while (!cmdList.isEmpty())
			{
				String httpCommand = cmdList.remove();
				System.out.println(httpCommand);
				writer.write(stringToAscii(httpCommand));
				writer.write(BLANK_LINE);
			}
			if (sentContent != null)
			{
				writer.write(sentContent);
			}
			writer.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			String message;
			while ((message = reader.readLine()) != null)
			{
				System.out.println(message);
				content += message + BLANK_LINE;
			}
			writer.close();
			client.close();
		}
		catch (Exception e)
		{
			System.out.println("Cannot get connect to address: " + sockAddr.getHostName());
			e.printStackTrace();
		}
		return new Page(content);
	}
	
	/*public Page getPage()
	{
		String content = null;
		try
		{
			client = new Socket();
			sockAddr = new InetSocketAddress(host, DEFAULT_PORT);
			//String hostName = InetAddress.getLocalHost().getHostName();
			client.connect(sockAddr, 1000);
			Writer writer = new OutputStreamWriter(client.getOutputStream());
			GET = GET + HOST + BLANK_LINE;
			writer.write(stringToAscii(GET));
			writer.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			String message;
			while ((message = reader.readLine()) != null)
			{
				content += message + BLANK_LINE;
			}
			writer.close();
			client.close();
		}
		catch (Exception e)
		{
			System.out.println("Cannot get connect to address: " + sockAddr.getHostName());
			e.printStackTrace();
		}
		return new Page(content);
	}*/

	private String stringToAscii(String str)
	{
		byte[] bytes = null;
		try
		{
			bytes = str.getBytes("US-ASCII");
		}
		catch (UnsupportedEncodingException e)
		{
			System.out.println("HELLO message not follows ASCII standard");
			e.printStackTrace();
		}
		if ((bytes == null) || (bytes.length == 0))
		{
			return "";
		}
		char[] ascii = new char[bytes.length];
		for (int i = 0; i < bytes.length; i++)
		{
			ascii[i] = (char) bytes[i];
		}
		return new String(ascii);
	}


/*	public Page send(String sentContent)
	{
		String content = null;
		try
		{
			client = new Socket();
			sockAddr = new InetSocketAddress(host, DEFAULT_PORT);
			client.connect(sockAddr, 1000);
			Writer writer;
			writer = new OutputStreamWriter(client.getOutputStream());
			POST = POST + BLANK_LINE;
			writer.write(stringToAscii(POST));
			writer.write(stringToAscii(sentContent));
			writer.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			String message;
			while ((message = reader.readLine()) != null)
			{
				content += message;
			}
		}
		catch (Exception e)
		{
			System.out.println("Fail to send message: " + sentContent + " to " + this.host);
			e.printStackTrace();
		}
		return new Page(content);
	}*/

}
