package bp.task;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bp.task.BPTaskFactory.BPTaskFactoryBase;
import bp.util.NetworkUtil;
import bp.util.NumberUtil;
import bp.util.ObjUtil;
import bp.util.NetworkUtil.NetworkSendResult;

public class BPTaskSendPing extends BPTaskLocal<Long>
{
	public BPTaskSendPing()
	{
		setCommonStatus(COMMON_STATUS_STOPPED);
	}

	public String getTaskName()
	{
		return "Send Ping";
	}

	@SuppressWarnings("unchecked")
	protected void doStart()
	{
		Map<String, Object> params = (Map<String, Object>) m_params;
		String host = (String) params.get("host");
		boolean nanosec = ObjUtil.toBool(params.get("nanosec"), false);
		setCommonStatus(COMMON_STATUS_RUNNING);
		InetAddress addr = null;
		try
		{
			addr = InetAddress.getByName(host);
		}
		catch (UnknownHostException e)
		{
			m_future.completeExceptionally(e);
			setFailed(e);
			return;
		}
		NetworkSendResult result = NetworkUtil.sendPing(addr, 10000, nanosec);
		m_future.complete(result.time);
		if (result.success)
			setCompleted("Time:" + NumberUtil.formatCurrency(result.time) + (nanosec ? "ns" : "ms"));
		else
			setCompleted("Unreacheable");
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

	public static class BPTaskFactorySendPing extends BPTaskFactoryBase<BPTaskSendPing>
	{
		public String getName()
		{
			return "Send Ping";
		}

		protected BPTaskSendPing createTask()
		{
			return new BPTaskSendPing();
		}

		public Class<? extends BPTask<?>> getTaskClass()
		{
			return BPTaskSendPing.class;
		}
	}
}
