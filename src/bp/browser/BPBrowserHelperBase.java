package bp.browser;

import java.util.Map;

import bp.env.BPEnv;
import bp.env.BPEnvBrowser;
import bp.env.BPEnvManager;
import bp.util.SystemUtil;

public class BPBrowserHelperBase implements BPBrowserHelper
{
	public int open(String browser, String url, Map<String, Object> options)
	{
		BPEnv env = BPEnvManager.getEnv(BPEnvBrowser.ENV_NAME_BROWSER);
		if (BPEnvBrowser.ENVKEY_BROWSER_DEFAULT.equals(browser))
			browser = env.getValue(browser);
		String path = env.getValue(browser);
		return SystemUtil.startSimpleProcess(path, null, makeParams(url, options));
	}

	protected String[] makeParams(String url, Map<String, Object> options)
	{
		return new String[] { url };
	}

	public boolean canHandle(String browser)
	{
		return true;
	}
}
