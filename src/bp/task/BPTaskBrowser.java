package bp.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import bp.browser.BPBrowserHelperManager;
import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;
import bp.env.BPEnv;
import bp.env.BPEnvBrowser;
import bp.env.BPEnvManager;
import bp.task.BPTaskFactory.BPTaskFactoryBase;
import bp.util.JSONUtil;
import bp.util.TextUtil;

public class BPTaskBrowser extends BPTaskLocal<Boolean>
{
	public BPTaskBrowser()
	{
		setCommonStatus(COMMON_STATUS_STOPPED);
	}

	public String getTaskName()
	{
		return "Browser";
	}

	protected Object mergeDynamicParams(Object sps, Object dps)
	{
		if (dps instanceof String)
		{
			Object[] ps = (Object[]) sps;
			Object[] newps = Arrays.copyOf(ps, ps.length);
			newps[1] = mergeURL(ps.length > 1 ? (String) ps[1] : null, (String) dps);
			return newps;
		}
		else
		{
			return super.mergeDynamicParams(sps, dps);
		}
	}

	protected String mergeURL(String surl, String durl)
	{
		if (surl == null || surl.length() == 0)
			return durl;
		return surl.replace("${param}", durl);
	}

	protected void doStart()
	{
		Object[] ps = (Object[]) m_params;
		String browser = (String) ps[0];
		String url = ps.length > 1 ? TextUtil.eds((String) ps[1]) : "about:blank";
		Map<String, Object> options = ps.length > 2 ? JSONUtil.decode((String) ps[2]) : null;

		setStarted();
		try
		{
			BPBrowserHelperManager.open(browser, url, options);
			m_future.complete(true);
			setCompleted();
		}
		catch (Exception e)
		{
			m_future.completeExceptionally(e);
			setFailed(e);
		}
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = super.getMappedData();
		Object[] ps = (Object[]) m_params;
		if (ps != null && ps.length > 1)
		{
			String browser = (String) ps[0];
			String url = ps.length > 1 ? TextUtil.eds((String) ps[1]) : null;
			String options = ps.length > 2 ? (String) ps[2] : null;

			rc.put("browser", browser);
			rc.put("url", url);
			rc.put("options", options);
		}
		return rc;
	}

	public void setMappedData(Map<String, Object> data)
	{
		super.setMappedData(data);
		String browser = (String) data.get("browser");
		String url = (String) data.get("url");
		String options = (String) data.get("options");

		m_params = new Object[] { browser, url, options };
	}

	public BPSetting getSetting()
	{
		List<String> browsers = new ArrayList<String>();
		{
			BPEnv env = BPEnvManager.getEnv(BPEnvBrowser.ENV_NAME_BROWSER);
			List<String> ks = env.listKeys();
			for (String k : ks)
			{
				if (!BPEnvBrowser.ENVKEY_BROWSER_DEFAULT.equals(k))
					browsers.add(k);
			}
		}
		BPSettingBase rc = new BPSettingBase();
		rc.addItem(BPSettingItem.create("browser", "Browser", BPSettingItem.ITEM_TYPE_SELECT, browsers.toArray(new String[browsers.size()])));
		rc.addItem(BPSettingItem.create("url", "URL", BPSettingItem.ITEM_TYPE_TEXT, null));
		rc.addItem(BPSettingItem.create("options", "Options", BPSettingItem.ITEM_TYPE_TEXT, null));
		return rc;
	}

	public static class BPTaskFactoryBrowser extends BPTaskFactoryBase<BPTaskBrowser>
	{
		public String getName()
		{
			return "Browser";
		}

		protected BPTaskBrowser createTask()
		{
			return new BPTaskBrowser();
		}

		public Class<? extends BPTask<?>> getTaskClass()
		{
			return BPTaskBrowser.class;
		}
	}
}