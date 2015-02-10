public class Url
{
	private String host = null;
	private int port = 80;
	private String path = null;
	private String protocol = "http/1.0";
	
	public Url(String websiteAddress)
	{
		host = getHostByAddress(websiteAddress);
		path = getPathByAddress(websiteAddress);
	}

	private String getPathByAddress(String websiteAddress)
	{
/*		String[] symbols = {"https://", "http://", "www."};
		for (String symbol:symbols)
		{
			System.out.println(websiteAddress.replaceAll(symbol,""));
		}*/
		return "/accounts/login/";
		//return "/";
		
	}

	private String getHostByAddress(String websiteAddress)
	{
		
		return "cs5700.ccs.neu.edu";
	}
	
	public String getHost()
	{
		return host;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public String getPath()
	{
		return path;
	}
	
	public String getProtocol()
	{
		return protocol;
	}
}
