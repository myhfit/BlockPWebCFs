package bp.browser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import bp.util.LogicUtil;
import bp.util.ObjUtil;
import bp.util.TextUtil;

public class BPBrowserHelperChromium extends BPBrowserHelperBase
{
	public final static CopyOnWriteArrayList<String> S_BSS = new CopyOnWriteArrayList<>(new String[] { "Chrome", "Chromium", "Edge" });

	public boolean canHandle(String browser)
	{
		return S_BSS.contains(browser);
	}

	protected String[] makeParams(String url, Map<String, Object> options)
	{
		boolean appmode = false;
		List<String> rc = new ArrayList<String>();
		if (options != null)
		{
			appmode = ObjUtil.toBool(options.get("appmode"), false);
			LogicUtil.IFVU(TextUtil.eds((String) options.get("size")), size -> rc.add("--window-size=" + size));
			LogicUtil.IFVU(TextUtil.eds((String) options.get("pos")), pos -> rc.add("--window-position=" + pos));
		}
		String urlp = appmode ? "--app=" + url : url;
		rc.add(urlp);
		return rc.toArray(new String[rc.size()]);
	}
}