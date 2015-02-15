package WebCrawler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;

/**
 * The Class HTTPconnection.
 */
public class HTTPconnection
{
	
	/** The DEFAULT protocol used */
	private String PROTOCOL = "HTTP/1.1";
	
	/** The DEFAULT connection. */
	private String CONNECTION = "close";
	
	/** The DEFAULT cache control. */
	private String CACHE_CONTROL = "max-age=0";
	
	/** The DEFAULT accept. */
	private String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
	
	/** The DEFAULT content type. */
	private String CONTENT_TYPE = "application/x-www-form-urlencoded";
	
	/** The DEFAULT user agent. */
	private String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.99 Safari/537.36";
	
	/** The DEFAULT accept lunguage. */
	private String ACCEPT_LUNGUAGE = "en-us";

	/** The current url fetched. */
	private Url currentURL;
	
	/** The linked list of request header. */
	private LinkedList<String> requestHearder;
	
	/** The linked list of posted message. */
	private LinkedList<String> postMessage;
	
	/** The Symbol of new line. */
	private String NEW_LINE = "\n";
	
	/** The socket client. */
	private Socket client = null;
	
	/** The sock addr. */
	private InetSocketAddress sockAddr = null;

	/**
	 * Instantiates a new HTTP connection.
	 * Create URL, requestHeader, and message body
	 * @param URL the url
	 */
	public HTTPconnection(Url URL)
	{
		this.currentURL = URL;
		requestHearder = new LinkedList<String>();
		postMessage = new LinkedList<String>();
	}

	/**
	 * Sets the request.
	 * Add new request in request header
	 * @param request the new request
	 */
	public void setRequest(String request)
	{
		String httpCommand = request + " " + currentURL.getPath() + " " + PROTOCOL + NEW_LINE;
		requestHearder.add(httpCommand);
	}

	/**
	 * Sets the header.
	 * Add new header in header list
	 * @param headerName the header name
	 * @param headerValue the header value
	 */
	public void setHeader(String headerName, String headerValue)
	{
		String header = headerName + ": " + headerValue + NEW_LINE;
		requestHearder.add(header);
	}

	/**
	 * Sets the default header.
	 */
	public void setDefaultHeader()
	{
		requestHearder.add("Connection: " + CONNECTION + NEW_LINE);
		requestHearder.add("Cache-Control: " + CACHE_CONTROL + NEW_LINE);
		requestHearder.add("ACCEPT: " + ACCEPT + NEW_LINE);
		requestHearder.add("CONTENT_TYPE: " + CONTENT_TYPE + NEW_LINE);
		requestHearder.add("USER_AGENT: " + USER_AGENT + NEW_LINE);
		requestHearder.add("ACCEPT_LUNGUAGE: " + ACCEPT_LUNGUAGE + NEW_LINE);
	}

	/**
	 * Sets the post content.
	 * Add new content POST message body
	 * @param sentContent the new post content
	 */
	public void setPostContent(String sentContent)
	{
		postMessage.add(sentContent);
	}

	/**
	 * Sets the cookies.
	 * Add more cookies in cookie list
	 * @param cookies the new cookies
	 */
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

	/**
	 * Process url.
	 * Processing URL according to the set of request, request header
	 * and message body (if there is any)
	 * @return the page
	 */
	public Page processURL()
	{
		String content = "";
		try
		{
			client = new Socket();
			sockAddr = new InetSocketAddress(currentURL.getHost(), currentURL.getPort());
			client.connect(sockAddr, 1000);
			Writer writer = new OutputStreamWriter(client.getOutputStream());
			while (!requestHearder.isEmpty())
			{
				String request = requestHearder.remove();
				writer.write(request);
			}
			writer.write(NEW_LINE);
			while (!postMessage.isEmpty())
			{
				String message = postMessage.remove();
				writer.write(message);
			}
			writer.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			String message = "";
			while ((message = reader.readLine()) != null)
			{
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
