package bp.res;

public class BPResourceWebSiteMenu extends BPResourceVirtual
{
	protected volatile String m_path;
	protected volatile Object m_userdata;

	public BPResourceWebSiteMenu()
	{

	}

	public BPResourceWebSiteMenu(BPResource parent)
	{
		m_parent = parent;
	}

	public void setPath(String path)
	{
		m_path = path;
	}

	public String getPath()
	{
		return m_path;
	}

	public void setName(String name)
	{
		m_name = name;
	}

	public String toString()
	{
		return m_name;
	}

	public void setLeaf(boolean flag)
	{
		m_isleaf = flag;
	}

	public void setCanOpen(boolean flag)
	{
		m_canopen = flag;
	}

	public void setCustomExt(String ext)
	{
		m_customext = ext;
	}

	public void setOpenWithTempID(boolean flag)
	{
		m_openwithtempid = flag;
	}

	@SuppressWarnings("unchecked")
	public <T> T getUserData()
	{
		return (T) m_userdata;
	}

	public void setUserData(Object userdata)
	{
		m_userdata = userdata;
	}
}
