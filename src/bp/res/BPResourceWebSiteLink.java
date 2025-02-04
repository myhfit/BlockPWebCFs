package bp.res;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bp.data.BPWebSiteAnalyzer;
import bp.util.ClassUtil;
import bp.util.IOUtil;
import bp.util.JSONUtil;
import bp.util.ObjUtil;
import bp.util.Std;
import bp.util.TextUtil;

public class BPResourceWebSiteLink extends BPResourceOverlay
{
	protected volatile String m_name;
	protected volatile String m_host;
	protected volatile String m_basepath;
	protected volatile int m_port;
	protected volatile String m_user;
	protected volatile String m_password;
	protected volatile String m_protocol;
	protected volatile String m_analyzerclass;
	protected volatile BPWebSiteAnalyzer m_analyzer;
	protected volatile List<BPResource> m_children;

	public final static String RESTYPE_WEBSITELINK = "websitelink";

	public BPResourceWebSiteLink(BPResourceFile fc)
	{
		super(fc);
	}

	public boolean readLinkFile()
	{
		Map<String, Object> params = ((BPResourceFile) m_res).useInputStream((in) ->
		{
			try
			{
				String str = TextUtil.toString(IOUtil.read(in), "utf-8");
				return JSONUtil.decode(str);
			}
			catch (Exception e)
			{
				Std.err(e);
			}
			return null;
		});
		if (params != null)
		{
			setMappedData(params);
			return true;
		}
		return false;
	}

	public String getName()
	{
		return m_name;
	}

	public boolean isFileSystem()
	{
		return false;
	}

	public boolean isLeaf()
	{
		return false;
	}

	public BPResource[] listResources(boolean isdelta)
	{
		if (m_children != null)
		{
			return m_children.toArray(new BPResource[m_children.size()]);
		}
		else
		{
			if (m_analyzerclass != null && m_analyzer == null)
				m_analyzer = ClassUtil.createObject(m_analyzerclass);
			if (m_analyzer != null)
			{
				return m_analyzer.getStructureWithAnalyzed(this);
			}
		}
		return null;
	}

	public boolean isProjectResource()
	{
		return true;
	}

	public String toString()
	{
		return m_name;
	}

	public String getProtocol()
	{
		return m_protocol;
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		rc.put("name", m_name);
		rc.put("host", m_host);
		rc.put("port", m_port);
		rc.put("user", m_user);
		rc.put("basepath", m_basepath);
		rc.put("password", m_password);
		rc.put("protocol", m_protocol);
		rc.put("analyzer", m_analyzerclass);
		return rc;
	}

	public void setMappedData(Map<String, Object> data)
	{
		m_name = (String) data.get("name");
		m_host = (String) data.get("host");
		m_port = ObjUtil.toInt(data.get("port"), 80);
		m_user = (String) data.get("user");
		m_password = (String) data.get("password");
		m_protocol = (String) data.get("protocol");
		m_analyzerclass = (String) data.get("analyzer");
		m_basepath = (String) data.get("basepath");
		if (m_protocol == null)
			m_protocol = "http";
		if (m_analyzerclass != null && m_analyzerclass.trim().length() == 0)
			m_analyzerclass = null;
	}

	public boolean rename(String newname)
	{
		return false;
	}

	public String getResType()
	{
		return RESTYPE_WEBSITELINK;
	}
}
