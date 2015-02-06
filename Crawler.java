import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements; 

public class Crawler
{
	private String startWebsite;
	private String userName;
	private String password;
	private String rule;
	
	private Queue<String> visitedWebsite;
	private Queue<String> unVisitedWebsite;
	private LinkedList<String> secretFlags;
	
	
	
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
	
	public Crawler(String startWebsite)
	{
		secretFlags = new LinkedList<String>();
		visitedWebsite = new LinkedList<String>();
		unVisitedWebsite = new LinkedList<String>();
		unVisitedWebsite.add(startWebsite);
	}

	// Return a linked list that consists of all the secret flags
	public LinkedList<String> Run() throws IOException
	{
		while (!unVisitedWebsite.isEmpty())
		{
			String currentWebsite = unVisitedWebsite.remove();
			URL currentCrawl = new URL(currentWebsite);
			URLConnection conn = currentCrawl.openConnection();
			//String response = conn.getHeaderField("Server");
			//System.out.println(response);
/*			Map<String, List<String>> map = conn.getHeaderFields();
			for (Map.Entry<String, List<String>> entry : map.entrySet()) {
				System.out.println("Key : " + entry.getKey() + 
		                 " ,Value : " + entry.getValue());
			}*/
			System.out.println(((HttpURLConnection) conn).getResponseCode());
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) 
			{
				//System.out.println(inputLine);
				Document document = Jsoup.parse(inputLine, "UTF-8");
				Document parse = Jsoup.parse(document.html());
				Elements links=parse.select("a");
				for (Element link : links) 
				{
	                System.out.println(link.attr("href"));
	            }  
			}
	        in.close();
			
			// Fetch Content
			// Search secret flags in the content, if finded, add it to the secretFlags
			// Search for all links in the content
			// For each URL, if matches rule && not in visitedWebsite nor unVisitedWebsite
			// add this URL to unVisitedWebsite
		}
		return secretFlags;
	}
}