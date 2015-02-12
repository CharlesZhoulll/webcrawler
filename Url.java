public class Url
{
	private String address = null;
	private String host = null;
	private int port = 80;
	private String path = null;
	private String query = null;
	
	public Url(String websiteAddress)
	{
		address = parse(websiteAddress);
		host = getHostByAddress(address);
		path = getPathByAddress(address);
		query = getStringQueryByAddress(address);
	}

	private String parse(String websiteAddress)
	{
		// RULES FOR URL'S ADDRESS
		// MUST START WITH http:// (we do not take https into consideration here)
		// does not contain www.
		// end with and "/"
		websiteAddress = websiteAddress.replace("www.", "");
		if (websiteAddress.indexOf("http://") == -1)
		{
			websiteAddress = "http://" + websiteAddress;
		}
		String lastChar = websiteAddress.substring(websiteAddress.length()-1);
		if (!lastChar.equals("/"))
		{
			websiteAddress = websiteAddress + "/";
		}
		return websiteAddress;
	}

	private String getPathByAddress(String websiteAddress)
	{
		websiteAddress =  websiteAddress.replace("http://", "");
		websiteAddress = websiteAddress.replace("www.", "");
		int index1 = websiteAddress.indexOf("/");
		if (index1 != -1)
		{
			int index2 = websiteAddress.indexOf("?");
			if (index2 == -1)
			{
				websiteAddress = websiteAddress.substring(index1);
			}
			else
			{
				websiteAddress = websiteAddress.substring(index1,index2);
			}
			return websiteAddress;
		}
		else
		{
			return "/";
		}
	}

	private String getStringQueryByAddress(String websiteAddress)
	{
		int index = websiteAddress.indexOf("?");
		if (index != -1)
		{
			websiteAddress = websiteAddress.substring(index+1);
			return websiteAddress;
		}
		else
		{
			return "";
		}
	}
	
	private String getHostByAddress(String websiteAddress)
	{
		websiteAddress =  websiteAddress.replace("http://", "");
		websiteAddress = websiteAddress.replace("www.", "");
		int index1 = websiteAddress.indexOf("/");
		if (index1 != -1)
		{
			websiteAddress = websiteAddress.substring(0, index1);
		}
		return websiteAddress;
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

	public String getQuery()
	{
		return query;
	}
	
	public String getOrigin()
	{
		// TODO Auto-generated method stub
		return "http://"+host;
	}

	public String getAddress()
	{
		return address;
	}
}
