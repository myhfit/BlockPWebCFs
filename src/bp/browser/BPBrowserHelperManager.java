package bp.browser;

import java.util.Map;

import bp.util.ClassUtil;

public class BPBrowserHelperManager
{
	public final static int open(String browser, String url, Map<String, Object> options)
	{
		BPBrowserHelper h = ClassUtil.findServiceByMaxScore(BPBrowserHelper.class, h2 -> h2.getHandleLevel(browser), h2 -> h2.canHandle(browser));
		if (h != null)
			return h.open(browser, url, options);
		return -1;
	}
}
