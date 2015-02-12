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
	private String PROTOCOL = "HTTP/1.1";
	private String CONNECTION = "close";
	private String CACHE_CONTROL = "max-age=0";
	private String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
	private String CONTENT_TYPE = "application/x-www-form-urlencoded";
	private String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.99 Safari/537.36";
	private String ACCEPT_LUNGUAGE = "en-us";

	private Url currentURL;
	private String request;
	private LinkedList<String> requestHearder;
	private LinkedList<String> postMessage;
	private String NEW_LINE = "\n";
	private Socket client = null;
	private InetSocketAddress sockAddr = null;

	public HTTPconnection(Url URL)
	{
		this.currentURL = URL;
		requestHearder = new LinkedList<String>();
		postMessage = new LinkedList<String>();
	}

	public void setRequest(String request)
	{
		this.request = request;
		String httpCommand = this.request + " " + currentURL.getPath() + " " + PROTOCOL + NEW_LINE;
		requestHearder.add(httpCommand);
	}

	public void setHeader(String headerName, String headerValue)
	{
		String header = headerName + ": " + headerValue + NEW_LINE;
		requestHearder.add(header);
	}

	public void setDefaultHeader()
	{
		requestHearder.add("Connection: " + CONNECTION + NEW_LINE);
		requestHearder.add("Cache-Control: " + CACHE_CONTROL + NEW_LINE);
		requestHearder.add("ACCEPT: " + ACCEPT + NEW_LINE);
		requestHearder.add("CONTENT_TYPE: " + CONTENT_TYPE + NEW_LINE);
		requestHearder.add("USER_AGENT: " + USER_AGENT + NEW_LINE);
		requestHearder.add("ACCEPT_LUNGUAGE: " + ACCEPT_LUNGUAGE + NEW_LINE);
	}

	public void setPostContent(String sentContent)
	{
		postMessage.add(sentContent);
	}

	public void setCookies(LinkedList<Cookie> cookies)
	{
		if (cookies != null)
		{
			String sentCookie = cookies.get(0).getName() + "=" + cookies.get(0).getValue();
			for (int i = 1; i < cookies.size(); i++)
			{
				sentCookie = sentCookie + "; " + cookies.get(i).getName() + "="
						+ cookies.get(i).getValue();
			}
			sentCookie = "Cookie: " + sentCookie + NEW_LINE;
			requestHearder.add(sentCookie);
			// System.out.println(sentCookie);
		}
	}

	public Page processURL()
	{
		String content = "";
		try
		{
			client = new Socket();
			sockAddr = new InetSocketAddress(currentURL.getHost(), currentURL.getPort());
			// String hostName = InetAddress.getLocalHost().getHostName();
			client.connect(sockAddr, 1000);
			Writer writer = new OutputStreamWriter(client.getOutputStream());
			while (!requestHearder.isEmpty())
			{
				String request = requestHearder.remove();
				//System.out.println(request);
				writer.write(request);
			}
			writer.write(NEW_LINE);
			while (!postMessage.isEmpty())
			{
				String message = postMessage.remove();
				// System.out.println(message);
				writer.write(message);
			}
			writer.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			String message = "";
			while ((message = reader.readLine()) != null)
			{
				//System.out.println(message);
				content += message + NEW_LINE;
			}
			writer.close();
			client.close();
		}
		catch (Exception e)
		{
			System.out.println("Cannot get connect to address: " + sockAddr.getHostName());
			e.printStackTrace();
		}
		Page currentPage = new Page(content);
		return currentPage;
	}
}
