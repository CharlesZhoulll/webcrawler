import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
	public LinkedList<String> Run()
	{
		while (!unVisitedWebsite.isEmpty())
		{
			String nowWebsite = unVisitedWebsite.remove();
			// Fetch Content
			// Search secret flags in the content, if finded, add it to the secretFlags
			// Search for all links in the content
			// For each URL, if matches rule && not in visitedWebsite nor unVisitedWebsite
			// add this URL to unVisitedWebsite
		}
		return secretFlags;
	}
}
