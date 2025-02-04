package bp.env;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BPEnvWeb extends BPEnvBase
{
	public final static String ENVKEY_WEB_UA = "UA";
	public final static String ENVKEY_WEB_ACCEPT = "ACCEPT";
	public final static String ENVKEY_WEB_ACCEPTLANG = "ACCEPT_LANG";

	public final static String ENV_NAME_WEB = "Web";

	protected List<String> m_rawkeys = new CopyOnWriteArrayList<String>(new String[] { ENVKEY_WEB_UA, ENVKEY_WEB_ACCEPT, ENVKEY_WEB_ACCEPTLANG });

	public BPEnvWeb()
	{
		m_kvs.put(ENVKEY_WEB_UA, "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/116.0");
		m_kvs.put(ENVKEY_WEB_ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
		m_kvs.put(ENVKEY_WEB_ACCEPTLANG, "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
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
		return ENV_NAME_WEB;
	}

	public boolean hasKey(String key)
	{
		return m_kvs.containsKey(key);
	}

	public boolean customKey()
	{
		return false;
	}
}
