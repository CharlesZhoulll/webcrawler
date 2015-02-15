package WebCrawler;

import java.io.IOException;
import java.util.LinkedList;

public class WebCrwlerProj2
{
	private static LinkedList<String> secretFlags;

	/**
	 * The main method.
	 *
	 * @param args Input argument, format: [username] [password]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	
	public static void main(String[] args) throws IOException
	{
		if (args.length < 2)
			throw new IllegalArgumentException("Please give me your username and website address !");
		String userName = args[0];
		String password = args[1];
		String startWebsite = "http://cs5700sp15.ccs.neu.edu/fakebook"; //cannot change this!
		String filter = "cs5700sp15.ccs.neu.edu"; 
		Crawler crawler = new Crawler();
		crawler.setUserName(userName);
		crawler.setPassword(password);
		crawler.setFilter(filter);
		secretFlags = crawler.findFlags(startWebsite);
		if (secretFlags != null)
		{
			for (String flags : secretFlags)
			{
				System.out.println(flags);
			}
		}
	}
}