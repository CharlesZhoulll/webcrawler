import java.io.IOException;
import java.util.LinkedList;

public class WebCrwlerProj2
{
	private static LinkedList<String> secretFlags;

	public static void main(String[] args) throws IOException
	{
		// initial web page
		// String s = args[0];
		String startWebsite = "http://cs5700f14.ccs.neu.edu";
		//String startWebsite = "https://accounts.google.com/ServiceLoginAuth";
		String userName = "001712808";
		String password = "ILFWMEKJ";
		String rule = "cs5700f14.ccs.neu.edu/fakebook"; // Need revise
		Crawler crawler = new Crawler();
		crawler.setUserName(userName);
		crawler.setPassword(password);
		crawler.setRule(rule);
		secretFlags = crawler.Run(startWebsite);
		for (String flags : secretFlags)
		{
			System.out.println(flags);
		}
	}
}