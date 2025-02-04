package bp.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import bp.env.BPEnv;
import bp.env.BPEnvManager;
import bp.env.BPEnvWeb;
import bp.format.BPFormat;
import bp.res.BPResourceWebSiteLink;

public class WebUtil
{
	public final static String URIEncodeComponent(String url)
	{
		if (url != null)
		{
			try
			{
				return URLEncoder.encode(url, "utf-8");
			}
			catch (UnsupportedEncodingException e)
			{
				Std.err(e);
			}
		}
		return null;
	}

	public final static String URIDecodeComponent(String url)
	{
		if (url != null)
		{
			try
			{
				return URLDecoder.decode(url, "utf-8");
			}
			catch (UnsupportedEncodingException e)
			{
				Std.err(e);
			}
		}
		return null;
	}

	public final static String getBaseURL(BPResourceWebSiteLink wslink)
	{
		Map<String, Object> wsmap = wslink.getMappedData();
		String host = (String) wsmap.get("host");
		String basepath = (String) wsmap.get("basepath");
		Integer port = (Integer) wsmap.get("port");
		String protocol = (String) wsmap.get("protocol");
		if (protocol == null)
			protocol = "http";
		if (port == null)
		{
			if ("http".equals(protocol))
			{
				port = 80;
			}
			else if ("https".equals(protocol))
			{
				port = 443;
			}
			else if ("ftp".equals(protocol))
			{
				port = 21;
			}
			else
			{
				port = 80;
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append(protocol + "://");
		sb.append(host);
		if (port != null)
			sb.append(":" + port);
		sb.append(basepath == null ? "" : basepath);
		return sb.toString();
	}

	public final static Map<String, String> getDefaultHeaderMap()
	{
		Map<String, String> rc = new HashMap<String, String>();
		BPEnv e = BPEnvManager.getEnv(BPEnvWeb.ENV_NAME_WEB);
		rc.put("User-Agent", e.getValue(BPEnvWeb.ENVKEY_WEB_UA));
		rc.put("Accept", e.getValue(BPEnvWeb.ENVKEY_WEB_ACCEPT));
		rc.put("Accept-Language", e.getValue(BPEnvWeb.ENVKEY_WEB_ACCEPTLANG));
		return rc;
	}

	public final static String[][] getDefaultHeaderFields()
	{
		BPEnv e = BPEnvManager.getEnv(BPEnvWeb.ENV_NAME_WEB);
		return new String[][] { new String[] { "User-Agent", e.getValue(BPEnvWeb.ENVKEY_WEB_UA) }, new String[] { "Accept", e.getValue(BPEnvWeb.ENVKEY_WEB_ACCEPT) }, new String[] { "Accept-Language", e.getValue(BPEnvWeb.ENVKEY_WEB_ACCEPTLANG) } };
	}

	public final static Map<String, String> queryStrToMap(String query)
	{
		Map<String, String> rc = new HashMap<String, String>();
		if (query != null && query.length() > 0)
		{
			String[] qs = query.split("&");
			for (String q : qs)
			{
				int vi = q.indexOf("=");
				if (vi > -1)
					rc.put(q.substring(0, vi), q.substring(vi + 1));
				else
					rc.put(q, null);
			}
		}
		return rc;
	}

	public final static String getMIMEFromFormat(BPFormat format)
	{
		String rc = null;
		if (format != null)
		{
			String[] exts = format.getExts();
			if (exts != null)
			{
				for (String ext : exts)
				{
					if (ext.indexOf("/") > 0)
					{
						rc = ext;
						break;
					}
				}
			}
		}
		return rc;
	}

	public final static Map<String, Object> getParamsFromQuery(String query)
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		if (query != null && query.length() > 0)
		{
			String[] qs = query.split("&");
			if (qs != null && qs.length > 0)
			{
				for (String q : qs)
				{
					int vi = q.indexOf("=");
					if (vi > -1)
					{
						String k = q.substring(0, vi);
						String v = q.substring(vi + 1);
						rc.put(k, URIDecodeComponent(v));
					}
					else
					{
						rc.put(q, null);
					}
				}
			}
		}
		return rc;
	}
}
