package bp.client;

import java.util.Map;

import bp.res.BPResourceWebSiteLink;
import bp.util.URLBuilder;

public class BPClientWebBase extends BPClientFreeCall
{
	protected volatile String m_host;
	protected volatile String m_basepath;
	protected volatile Integer m_port;
	protected volatile String m_scheme;
	protected volatile String m_username;
	protected volatile String m_password;
	protected volatile String m_wslinkname;
	protected volatile String m_querystr;

	public void setup(BPResourceWebSiteLink wslink)
	{
		m_wslinkname = wslink.getName();
		Map<String, Object> wsmap = wslink.getMappedData();
		m_host = (String) wsmap.get("host");
		m_basepath = (String) wsmap.get("basepath");
		m_port = (Integer) wsmap.get("port");
		m_scheme = (String) wsmap.get("protocol");
		m_username = (String) wsmap.get("user");
		m_password = (String) wsmap.get("password");
	}

	public void setupNotEmpty(String scheme, String host, Integer port, String username, String password, String basepath)
	{
		if (scheme != null)
			m_scheme = scheme;
		if (host != null)
			m_host = host;
		if (port != null)
			m_port = port;
		if (username != null)
			m_username = username;
		if (password != null)
			m_password = password;
		if (basepath != null)
			m_basepath = basepath;
	}

	public void setup(String scheme, String host, Integer port, String username, String password, String basepath)
	{
		m_scheme = scheme;
		m_host = host;
		m_port = port;
		m_username = username;
		m_password = password;
		m_basepath = basepath;
	}

	public String getBaseURL()
	{
		return new URLBuilder().scheme(m_scheme).user(m_username).password(m_password).host(m_host).port(m_port).path(m_basepath).getURL();
	}
}
