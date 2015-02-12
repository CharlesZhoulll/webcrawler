import java.util.HashMap;
import java.util.LinkedList;

public class Cookie
{
	private HashMap<String, String> cookie;  // <Name, Value>
	private String name;
	private String value;
	
	
	public Cookie(String cookieName, String cookieValue)
	{
		cookie = new HashMap<String, String>();
		name = cookieName;
		value = cookieValue;
		cookie.put(name, value);
	}

	public String getName()
	{
		return name;
	}
 
	public String getValue()
	{
		return value;
	}
	
	public void setValue(String newValue)
	{
		value = newValue;
	}
}
