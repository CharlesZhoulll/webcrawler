import java.util.Queue;
import java.util.LinkedList;

public class Crawler
{
	private final int MAX_TRY = 3;
	private int numberTried = 0;

	private final int HTTP_OK = 200;
	private final int HTTP_FOUND = 302;
	private final int HTTP_MOVED = 301;
	private final int HTTP_FORBIDDEN = 403;
	private final int HTTP_NOTFOUND = 404;
	private final int HTTP_SERVER_ERROR = 502;

	private String userName;
	private String password;
	private String filter;

	private Queue<String> visitedWebsite;
	private Queue<String> unVisitedWebsite;
	private Url currentWebsite;
	
	private LinkedList<String> secretFlags;
	private LinkedList<Cookie> cookies;

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public void setFilter(String filter)
	{
		this.filter = filter;
	}

	public Crawler()
	{
		secretFlags = new LinkedList<String>();
		visitedWebsite = new LinkedList<String>();
		unVisitedWebsite = new LinkedList<String>();
	}

	// Return a linked list that consists of all the secret flags
	public LinkedList<String> Run(String startWebsite)
	{
		//currentWebsite = new Url(startWebsite);
		if (!login(startWebsite, userName, password))
		{
			System.out.println("Login fail! Please check your username and password!");
			return null;
		}
		unVisitedWebsite.add(currentWebsite.getAddress());
		while (!unVisitedWebsite.isEmpty())
		{
			String currentWebsiteAddress = unVisitedWebsite.remove();
			//System.out.println("Now fetching: " + currentWebsiteAddress);
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

}