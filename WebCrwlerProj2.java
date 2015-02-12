import java.io.IOException;
import java.util.LinkedList;

public class WebCrwlerProj2
{
	private static LinkedList<String> secretFlags;

	public static void main(String[] args) throws IOException
	{
		String startWebsite = "http://cs5700sp15.ccs.neu.edu/fakebook"; //cannot change this! Need revise
		String userName = "001712808";
		String password = "ILFWMEKJ";
		String filter = "cs5700sp15.ccs.neu.edu"; 
		Crawler crawler = new Crawler();
		crawler.setUserName(userName);
		crawler.setPassword(password);
		crawler.setFilter(filter);
		secretFlags = crawler.Run(startWebsite);
		if (secretFlags != null)
		{
			for (String flags : secretFlags)
			{
				System.out.println(flags);
			}
		}
	}
}