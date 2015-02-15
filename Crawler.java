package WebCrawler;

import java.util.Queue;
import java.util.LinkedList;

/**
 * The Class Crawler.
 */
public class Crawler
{
	
	/** Max retry times when have internal server error. */
	private final int MAX_TRY = 3;
	
	/** The number of times that has tried. */
	private int numberTried = 0;

	/** The http status OK. */
	private final int HTTP_OK = 200;
	
	/** The http status FOUND. */
	private final int HTTP_FOUND = 302;
	
	/** The http status MOVED. */
	private final int HTTP_MOVED = 301;
	
	/** The http status FORBIDDEN. */
	private final int HTTP_FORBIDDEN = 403;
	
	/** The http status NOT FOUND. */
	private final int HTTP_NOTFOUND = 404;
	
	/** The http status SERVER ERROR. */
	private final int HTTP_SERVER_ERROR = 502;

	/** The user name. */
	private String userName;
	
	/** The password. */
	private String password;
	
	/** The filter. */
	private String filter;

	/** The visited website. */
	private Queue<String> visitedWebsite;
	
	/** The un-visited website. */
	private Queue<String> unVisitedWebsite;
	
	/** The current fetching website. */
	private Url currentWebsite;
	
	/** The secret flags. */
	private LinkedList<String> secretFlags;
	
	/** The cookies. */
	private LinkedList<Cookie> cookies;

	/**
	 * Sets the user name.
	 *
	 * @param userName the user name
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	/**
	 * Sets the password.
	 *
	 * @param password the password
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * Sets the filter.
	 * The address that does not contain filter will be ignored
	 * @param filter the filter
	 */
	public void setFilter(String filter)
	{
		this.filter = filter;
	}

	/**
	 * Instantiates a new crawler.
	 */
	public Crawler()
	{
		secretFlags = new LinkedList<String>();
		visitedWebsite = new LinkedList<String>();
		unVisitedWebsite = new LinkedList<String>();
	}

	// Return a linked list that consists of all the secret flags
	/**
	 * findFlags.
	 * @param startWebsite the start website
	 * @return the linked list with all secret flags found
	 */
	public LinkedList<String> findFlags(String startWebsite)
	{
		if (!login(startWebsite, userName, password))
		{
			System.out.println("Login fail! Please check your username and password!");
			return null;
		}
		unVisitedWebsite.add(currentWebsite.getAddress());
		while (!unVisitedWebsite.isEmpty())
		{
			String currentWebsiteAddress = unVisitedWebsite.remove();
			visitedWebsite.add(currentWebsiteAddress);
			currentWebsite = new Url(currentWebsiteAddress);
			Page currentPage = getPageContent(currentWebsite);
			int responseCode = currentPage.getResponseCode();
			if (responseCode != HTTP_OK)
			{
				currentPage = handleStatus(currentWebsite, currentPage, numberTried);
			}
			if (currentPage != null)
			{
				secretFlags.addAll(currentPage.getSecretFlags());
				if (secretFlags.size() == 5)
					break;
				LinkedList<String> links = currentPage.getLinks();
				if (links != null)
				{
					for (String link : links)
					{
						processLink(link);
					}
				}
			}
		}
		return secretFlags;
	}

	/**
	 * Process link.
	 * Judge whether the link has been visited or has been 
	 * put into unvisitedwebsite list or not. If not, parse the link
	 * and put it into unvisitedWebsite list
	 * @param link: the link need to be processed
	 * 
	 */
	private void processLink(String link)
	{
		int index = link.indexOf("/");
		// If there is no host
		if (index == 0)
		{
			link = currentWebsite.getHost() + link; // Add it with host
		}
		if (filter != null)
		{
			if (link.indexOf(filter) != -1)
			{
				Url candidate = new Url(link);
				String parsedAddress = candidate.getAddress();
				if (!visitedWebsite.contains(parsedAddress)
						&& !unVisitedWebsite.contains(parsedAddress))
				{
					unVisitedWebsite.add(parsedAddress);
				}
			}
		}
	}

	/**
	 * Login.
	 * Try to login in the startWebsite using userName and password provided
	 * Only suitable for the specific website. As we specify the format of POST content.
	 * @param startWebsite the start website
	 * @param userName the user name
	 * @param password the password
	 * @return true if login successful, false otherwise
	 */
	private Boolean login(String startWebsite, String userName, String password)
	{
		currentWebsite = new Url(startWebsite);
		Page pageBeforeLogin = getPageContent(currentWebsite);
		int responseCode = pageBeforeLogin.getResponseCode();
		if (responseCode != HTTP_OK)
		{
			pageBeforeLogin = handleStatus(currentWebsite, pageBeforeLogin, numberTried);
		}
		if (pageBeforeLogin != null)
		{
			String sentContent = getSentContent(userName, password, pageBeforeLogin, currentWebsite);
			Page pageAfterLogin = sendPageContent(currentWebsite, sentContent);
			responseCode = pageAfterLogin.getResponseCode();
			if (responseCode != HTTP_OK)
			{
				pageAfterLogin = handleStatus(currentWebsite, pageAfterLogin, numberTried);
				if (pageAfterLogin == null)
				{
					return false;
				}
			}
			if (currentWebsite.getAddress().equals(startWebsite)
					|| currentWebsite.getAddress().equals(startWebsite + "/"))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	/**
	 * Gets the sent content.
	 * The format of sent content is "username=**&password=**&csrfmiddlewaretoken=**&String Query"
	 * @param userName the user name
	 * @param passWord the pass word
	 * @param page the page
	 * @param URL the url
	 * @return the sent content
	 */
	private String getSentContent(String userName, String passWord, Page page, Url URL)
	{
		String contentPart1 = "username=" + userName;
		String contentPart2 = "password=" + passWord;
		String contentPart3 = "csrfmiddlewaretoken=" + page.getCsrfCode();
		String contentPart4 = URL.getQuery();
		String contentSent = contentPart1 + "&" + contentPart2 + "&" + contentPart3 + "&"
				+ contentPart4;
		return contentSent;
	}

	/**
	 * Handle status.
	 * If redirected, get the location of redirected link, refetch it and return new page
	 * If not found or forbidden, ignore this link and return null
	 * If server error, try to refetch it and return new page 
	 * @param URL the url
	 * @param page the page
	 * @param numberTried the number tried
	 * @return new page after handle the exception
	 */
	private Page handleStatus(Url URL, Page page, int numberTried)
	{
		if (++numberTried > MAX_TRY)
		{
			numberTried = 0;
			return null;
		}
		int responseCode = page.getResponseCode();
		if (responseCode == HTTP_FOUND || responseCode == HTTP_MOVED)
		{
			String redirectedWebsite = page.getLocation();
			currentWebsite = new Url(redirectedWebsite);
			Page newPage = getPageContent(currentWebsite);
			int newResponseCode = newPage.getResponseCode();
			if (newResponseCode != HTTP_OK)
			{
				return handleStatus(URL, newPage, numberTried);
			}
			else
			{
				numberTried = 0;
				return newPage;
			}
		}
		
		if (responseCode == HTTP_NOTFOUND || responseCode == HTTP_FORBIDDEN)
		{
			numberTried = 0;
			return null;
		}
		
		if (responseCode == HTTP_SERVER_ERROR)
		{
			System.out.println("Internetal server error, try again...");
			Page newPage = getPageContent(URL);
			int newResponseCode = newPage.getResponseCode();
			if (newResponseCode != HTTP_OK)
			{
				return handleStatus(URL, newPage, numberTried);
			}
			else
			{
				numberTried = 0;
				return newPage;
			}
		}
		numberTried = 0;
		return null;
	}

	/**
	 * Send page content.
	 * Set sending parameters, like Host, origin, Referer, content-length, etc. 
	 * Send default headers
	 * Send cookies (important)
	 * @param URL the url
	 * @param sentContent the sent content
	 * @return the page
	 */
	private Page sendPageContent(Url URL, String sentContent)
	{
		HTTPconnection conn = new HTTPconnection(URL);
		conn.setRequest("POST");
		conn.setHeader("Host", URL.getHost());
		conn.setHeader("Origin", URL.getOrigin());
		conn.setHeader("Referer", URL.getAddress());
		conn.setHeader("Content-Length", Integer.toString(sentContent.length()));
		conn.setCookies(cookies);
		conn.setDefaultHeader();
		conn.setPostContent(sentContent);
		Page page = conn.processURL();
		addCookies(page.getCookies());
		return page;
	}

	/**
	 * Gets the page content.
	 * Setting get parameters, like Host and cookies
	 * Set all other default headers
	 * @param URL the url
	 * @return the page content
	 */
	private Page getPageContent(Url URL)
	{
		HTTPconnection conn = new HTTPconnection(URL);
		conn.setRequest("GET");
		conn.setHeader("Host", URL.getHost());
		conn.setCookies(cookies);
		conn.setDefaultHeader();
		Page page = conn.processURL();
		addCookies(page.getCookies());
		return page;
	}
	
	/**
	 * Adds the cookies.
	 * Add new cookie get from the current link to cookie list.
	 * If the cookie has existed n hte list, update its value
	 * @param newCookies the new cookies
	 */
	private void addCookies(LinkedList<Cookie> newCookies)
	{
		if (newCookies != null)
		{
			if (cookies == null)
			{
				cookies = newCookies;
			}
			else
			{
				for (Cookie newCookie : newCookies)
				{
					for (Cookie originalCookie : cookies)
					{
						if (newCookie.getName().equals(originalCookie.getName()))
						{
							cookies.remove(originalCookie.getName());
							break;
						}
					}
					cookies.add(newCookie);
				}
			}
		}
	}

}