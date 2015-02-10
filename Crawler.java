import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.*;
import java.io.*;

import javax.net.ssl.HttpsURLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler
{
	private final int HTTP_OK = 200;
	private String userName;
	private String password;
	private String rule;

	private Queue<String> visitedWebsite;
	private Queue<String> unVisitedWebsite;
	private LinkedList<String> secretFlags;
	URLConnection conn;
	private List<String> cookies;

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public void setRule(String rule)
	{
		this.rule = rule;
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
		Url startURL = new Url(startWebsite);
		int xx = login(startURL, userName, password);
		System.out.println(xx);
		while (!unVisitedWebsite.isEmpty())
		{
			Url currentWebsite = new Url(unVisitedWebsite.remove());
			HTTPconnection conn = new HTTPconnection(currentWebsite);
			conn.setCmd("GET");
			Page page = conn.processURL();
			int responseCode = page.getResponseCode();
			if (responseCode == HTTP_OK)
			{
				LinkedList<String> flags = page.getSecretFlags();
				LinkedList<String> links = page.getLinks();
				if(flags != null)
				{
					for (String flag:flags)
					{
						secretFlags.add(flag);
					}
				}
				if (links != null)
				{
					for (String link:links)
					{
						System.out.println(link);
					}
				}
			}
			else
			{
				System.out.println(responseCode);
			}
			
			// System.out.println(result);
			// Fetch Content
			// Search secret flags in the content, if finded, add it to the
			// secretFlags
			// Search for all links in the content
			// For each URL, if matches rule && not in visitedWebsite nor
			// unVisitedWebsite
			// add this URL to unVisitedWebsite
		}
		/*
		 * else { System.out.println("Login failure! Error Code: " +
		 * responseCode); }
		 */
		return secretFlags;
	}

	private int login(Url startWebsite, String userName, String password)
	{
		HTTPconnection conn = new HTTPconnection(startWebsite);
		conn.setCmd("GET");
		Page pageBeforeLogin = conn.processURL();
		if (pageBeforeLogin.getResponseCode() == HTTP_OK)
		{
			String sentUserName = "username=" + userName;
			String sentPassword = "password=" + password;
			String sentCsrfCode = "csrfmiddlewaretoken=" + pageBeforeLogin.getCsrfCode();
			String sentContent = sentUserName + "&" + sentPassword + "&" + sentCsrfCode;
			conn.setCmd("POST");
			conn.setPostContent(sentContent);
			Page pageAfterLogin = conn.processURL();
			return pageAfterLogin.getResponseCode();
		}
		else
		{
			return pageBeforeLogin.getResponseCode();
		}
	}

/*	private String streamToString(BufferedReader in)
	{
		StringBuilder sb = new StringBuilder();
		String line;
		try
		{

			while ((line = in.readLine()) != null)
			{
				sb.append(line);
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return sb.toString();
	}*/
}