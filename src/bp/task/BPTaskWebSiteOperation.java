package bp.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import bp.BPCore;
import bp.res.BPResourceFile;
import bp.res.BPResourceWebSiteLink;
import bp.task.BPTaskFactory.BPTaskFactoryBase;
import bp.util.IOUtil;
import bp.util.Std;
import bp.util.TextUtil;
import bp.web.BPWebContext;
import bp.web.BPWebContextBase;
import bp.web.BPWebOperation;
import bp.web.BPWebOperationBase;

public class BPTaskWebSiteOperation extends BPTaskLocal<Boolean>
{
	public BPTaskWebSiteOperation()
	{
		setCommonStatus(COMMON_STATUS_STOPPED);
	}

	public String getTaskName()
	{
		return "WebSite Operation";
	}

	@SuppressWarnings("unchecked")
	protected void doStart()
	{
		Map<String, Object> params = (Map<String, Object>) m_params;
		String wslinkfilename = (String) params.get("wslinkfilename");
		String wsopfilename = (String) params.get("wsopfilename");
		BPResourceWebSiteLink wslink = new BPResourceWebSiteLink((BPResourceFile) BPCore.getFileContext().getRes(wslinkfilename));
		wslink.readLinkFile();
		BPResourceFile wsopfile = (BPResourceFile) BPCore.getFileContext().getRes(wsopfilename);
		String wsop = wsopfile.useInputStream((in) -> TextUtil.toString(IOUtil.read(in), "utf-8"));
		runWSOP(wsop, wslink);
	}

	protected boolean runWSOP(String wsop, BPResourceWebSiteLink wslink)
	{
		BPWebContext context = new BPWebContextBase(wslink);
		if (context != null)
		{
			setStarted();
			triggerStatusChanged();
			try
			{
				String[] linearr = wsop.split("\n");
				List<BPWebOperation> ops = new ArrayList<BPWebOperation>();
				for (String line : linearr)
				{
					BPWebOperation op;
					if (line.endsWith("\r"))
						op = BPWebOperationBase.parse(line.substring(0, line.length() - 1));
					else
						op = BPWebOperationBase.parse(line);
					if (op != null)
						ops.add(op);
				}
				linearr = null;
				int c = 0;
				int all = ops.size();
				for (BPWebOperation op : ops)
				{
					try
					{
						context.operate(op).whenComplete((resp, t) ->
						{
							if (t != null)
								Std.err(t);
						}).toCompletableFuture().get();
						c++;
						setProgress((float) c / (float) all);
						setProgressText(c + "/" + all);
						triggerStatusChanged();
					}
					catch (InterruptedException | ExecutionException e)
					{
						Std.err(e);
					}
				}
			}
			finally
			{
				context.shutdown();
				setCompleted();
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = super.getMappedData();
		rc.putAll((Map<String, ?>) m_params);
		return rc;
	}

	public void setMappedData(Map<String, Object> data)
	{
		super.setMappedData(data);
		m_params = new ConcurrentHashMap<String, Object>(data);
	}

	public static class BPTaskFactoryWebSiteOperation extends BPTaskFactoryBase<BPTaskWebSiteOperation>
	{
		public String getName()
		{
			return "WebSite Operation";
		}

		protected BPTaskWebSiteOperation createTask()
		{
			return new BPTaskWebSiteOperation();
		}

		public Class<? extends BPTask<?>> getTaskClass()
		{
			return BPTaskWebSiteOperation.class;
		}
	}
}
