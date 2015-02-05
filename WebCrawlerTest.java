import java.util.LinkedList;

public class WebCrawlerTest
{
	private static LinkedList<String> secretFlags;
	public static void main(String[] args)
	{
		// initial web page
		//String s = args[0];
		String startWebsite = "http://cs5700.ccs.neu.edu/fakebook/";
		String userName = "charleszhoulll";
		String password = "qzmp1991327";
		String rule = "cs5700.ccs.neu.edu/fakebook"; // Need revise
		Crawler crawler = new Crawler(startWebsite);
		crawler.setUserName(userName);
		crawler.setPassword(password);
		crawler.setRule(rule);
		secretFlags = crawler.Run();
		for(String flags:secretFlags)
		{
			System.out.println(flags);
		}
	}
}
