package bp.env;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BPEnvBrowser extends BPEnvBase
{
	public final static String ENVKEY_BROWSER_DEFAULT = "DEFAULT";

	public final static String ENV_NAME_BROWSER = "Browser";

	protected List<String> m_rawkeys = new CopyOnWriteArrayList<String>(new String[] { ENVKEY_BROWSER_DEFAULT });

	public BPEnvBrowser()
	{
		m_kvs.put(ENVKEY_BROWSER_DEFAULT, "Chromium");
		m_kvs.put("Chromium", "");
		m_kvs.put("Chrome", "");
		m_kvs.put("Edge", "");
		m_kvs.put("Firefox", "");
		m_kvs.put("IE", "");
		m_kvs.put("Konqueror", "");
		m_kvs.put("Safari", "");
	}

	public List<String> listRawKeys()
	{
		return m_rawkeys;
	}

	public boolean isRawKey(String key)
	{
		return m_rawkeys.contains(key);
	}

	public String getName()
	{
		return ENV_NAME_BROWSER;
	}

	public boolean hasKey(String key)
	{
		return m_kvs.containsKey(key);
	}

	public boolean customKey()
	{
		return true;
	}
}
