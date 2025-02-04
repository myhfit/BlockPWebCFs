package bp.util;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class URLBuilder
{
	protected String m_host;
	protected String m_path;
	protected Integer m_port;
	protected String m_scheme;
	protected String m_user;
	protected String m_password;
	protected String m_querystr;
	protected Map<String, List<String>> m_querymap = new LinkedHashMap<String, List<String>>(4);

	public URLBuilder host(String host)
	{
		m_host = host;
		return this;
	}

	public URLBuilder path(String path)
	{
		m_path = path;
		return this;
	}

	public URLBuilder port(Integer port)
	{
		if (port != null)
			m_port = port;
		return this;
	}

	public URLBuilder scheme(String scheme)
	{
		m_scheme = scheme;
		return this;
	}

	public URLBuilder user(String user)
	{
		m_user = user;
		return this;
	}

	public URLBuilder password(String password)
	{
		m_password = password;
		return this;
	}

	public URLBuilder queryStr(String querystr)
	{
		m_querystr = querystr;
		return this;
	}

	public URLBuilder addQueryParam(String key, String value)
	{
		List<String> vs = m_querymap.get(key);
		if (vs == null)
		{
			vs = new LinkedList<String>();
			m_querymap.put(key, vs);
		}
		vs.add(value);
		return this;
	}

	public URLBuilder removeQueryParam(String key)
	{
		m_querymap.remove(key);
		return this;
	}

	public String getURL()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(m_scheme == null ? "http" : m_scheme);
		sb.append("://");
		if (m_user != null)
		{
			sb.append(m_user);
			if (m_password != null)
			{
				sb.append(":");
				sb.append(m_password);
			}
			sb.append("@");
		}
		sb.append(m_host);
		if (m_port != null)
		{
			sb.append(":");
			sb.append((int) m_port);
		}
		if (m_path != null)
			sb.append(m_path);
		boolean qs = m_querystr != null;
		boolean qm = m_querymap.size() > 0;
		if (qs || qm)
		{
			sb.append("?");
			if (qs)
			{
				sb.append(m_querystr);
			}
			if (qm)
			{
				boolean tf = qs;
				for (Entry<String, List<String>> entry : m_querymap.entrySet())
				{
					List<String> vs = entry.getValue();
					if (vs == null || vs.size() == 0)
					{
						if (tf)
						{
							sb.append("&");
						}
						else
						{
							tf = true;
						}
						sb.append(entry.getKey());
					}
					else
					{
						for (String v : vs)
						{
							if (tf)
							{
								sb.append("&");
							}
							else
							{
								tf = true;
							}
							sb.append(entry.getKey());
							if (v != null)
							{
								sb.append("=");
								sb.append(WebUtil.URIEncodeComponent(v));
							}
						}
					}
				}
			}
		}

		return sb.toString();
	}

	public String toString()
	{
		return getURL();
	}
}
