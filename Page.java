import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Page
{
	private String head;
	private int responseCode;
	private Document body;

	public Page(String content)
	{
		try
		{
			int startIndexOfBody = content.indexOf("<html");
			head = content.substring(4, startIndexOfBody).trim(); // Get rid of null
			dividHead(head);
			body = Jsoup.parse(content.substring(startIndexOfBody).trim());
		}
		catch (Exception e)
		{
			System.out.println("Cannot recognize the page");
			e.printStackTrace();
		}
	}

	private void dividHead(String head)
	{
		String delimsNewLine = "\n+";
		String[] headPerLine = head.split(delimsNewLine);
		String delimsSpace = "[ ]+";
		String[] headFirstLine = headPerLine[0].split(delimsSpace);
		responseCode = Integer.parseInt(headFirstLine[1]);
	}

	public int getResponseCode()
	{
		return responseCode;
	}

	public LinkedList<String> getSecretFlags()
	{
		Elements secretFlagsEle = body.select("secret_flag");
		LinkedList<String> flags = new LinkedList<String>();;
		for (Element flag: secretFlagsEle)
		{
			flags.add(flag.text());
		}
		return flags;
	}

	public LinkedList<String> getLinks()
	{
		Elements linksEle = body.select("a[href]");
		LinkedList<String> links =  new LinkedList<String>();
		for (Element link: linksEle)
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

}
