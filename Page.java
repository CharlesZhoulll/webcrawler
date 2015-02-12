import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Page
{
	private String headers;
	private HashMap<String, String> header;
	private String protocol;

	private int responseCode;
	private Document body;

	public Page(String content)
	{
		if (content != null)
		{
			headers = content.substring(0, content.indexOf("\n\n")).trim();
			header = new HashMap<String, String>();
			processHeader(headers);
			body = Jsoup.parse(content.substring(content.indexOf("\n\n")).trim());
		}
		else
		{
			System.out.println("axxxx");
		}
	}

	private void processHeader(String headers)
	{
		String delimsNewLine = "\n+";
		String[] headPerLine = headers.split(delimsNewLine);
		String delimsSpace = "[ ]+";
		String[] headFirstLine = headPerLine[0].split(delimsSpace);
		responseCode = Integer.parseInt(headFirstLine[1]);
		protocol = headFirstLine[0];
		for (int i = 1; i < headPerLine.length; i++)
		{
			String key = headPerLine[i].substring(0, headPerLine[i].indexOf(":")).trim();
			String value = headPerLine[i].substring(headPerLine[i].indexOf(":") + 2).trim();
			if (header.containsKey(key))
			{
				value = header.get(key) + "||" + value; // Just for cookies
			}
			header.put(key, value);
		}
	}

	public int getResponseCode()
	{
		return responseCode;
	}

	public String getProtocol()
	{
		return protocol;
	}

	public LinkedList<String> getSecretFlags()
	{
		Elements secretFlagsEle = body.select("h2.secret_flag");
		LinkedList<String> flags = new LinkedList<String>();
		for (Element flag : secretFlagsEle)
		{
			//System.out.println(flag.text());
			flags.add(flag.text());
		}
		return flags;
	}

	public LinkedList<String> getLinks()
	{
		Elements linksEle = body.select("a[href]");
		LinkedList<String> links = new LinkedList<String>();
		for (Element link : linksEle)
		{
			links.add(link.attr("href"));
		}
		return links;
	}

	public String getCsrfCode()
	{
		Elements inputElements = body.getElementsByTag("input");
		for (Element inputElement : inputElements)
		{
			String nameOfInputElement = inputElement.attr("name");
			if (nameOfInputElement.equals("csrfmiddlewaretoken"))
			{
				return inputElement.attr("value");
			}
		}
		return null;
	}

	public LinkedList<Cookie> getCookies()
	{
		LinkedList<Cookie> cookieList = new LinkedList<Cookie>();
		for (HashMap.Entry<String, String> entry : header.entrySet())
		{
			String key = entry.getKey();
			if (key.equals("Set-Cookie"))
			{
				String allCookies = (String) entry.getValue();
				String delim = "[||]+";
				String[] cookies = allCookies.split(delim);
				for (String everyCookieContent:cookies)
				{
					String cookieName = everyCookieContent.substring(0, everyCookieContent.indexOf("="));	
					String cookieValue = everyCookieContent.substring(everyCookieContent.indexOf("=")+1, everyCookieContent.indexOf(";"));	
					Cookie cookie = new Cookie(cookieName, cookieValue);
					cookieList.add(cookie);
				}
				// To be add, set other properties of cookie
			}
		}
		if (cookieList.size() > 0)
		{

			return cookieList;
		}
		else
		{
			return null;
		}
	}

	public Document getBody()
	{
		return body;
	}
	
	public String getLocation()
	{
		return header.get("Location");
	}

}
