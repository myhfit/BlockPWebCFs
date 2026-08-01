package bp.env;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BPEnvBrowser extends BPEnvCustom
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

	public String getName()
	{
		return ENV_NAME_BROWSER;
	}

	protected List<String> setupRawKeys()
	{
		return new CopyOnWriteArrayList<String>(new String[] { ENVKEY_BROWSER_DEFAULT });
	}
}
