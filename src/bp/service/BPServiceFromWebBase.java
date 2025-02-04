package bp.service;

import java.net.MalformedURLException;
import java.net.URL;

import bp.util.Std;
import bp.util.URLBuilder;

public abstract class BPServiceFromWebBase extends BPServiceFreeCall implements BPServiceFromWeb
{
	protected volatile String m_host;
	protected volatile String m_path;
	protected volatile Integer m_port;
	protected volatile String m_scheme;
	protected volatile String m_querystr;

	public String getBaseURL()
	{
		return new URLBuilder().scheme(m_scheme).host(m_host).port(m_port).path(m_path).queryStr(m_querystr).getURL();
	}

	public void setBaseURL(String baseurl)
	{
		try
		{
			URL url = new URL(baseurl);
			m_host = url.getHost();
			m_path = url.getPath();
			int p = url.getPort();
			if (p > -1)
				m_port = p;
			m_querystr = url.getQuery();
		}
		catch (MalformedURLException e)
		{
			Std.err(e);
		}
	}
}
