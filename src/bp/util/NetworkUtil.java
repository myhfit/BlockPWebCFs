package bp.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkUtil
{
	public final static InetAddress getAddress(String ipstr)
	{
		if (ipstr == null)
			return null;
		ipstr = ipstr.trim();
		if (ipstr.length() == 0)
			return null;
		try
		{
			return InetAddress.getByName(ipstr);
		}
		catch (UnknownHostException e)
		{
			Std.err(e);
			return null;
		}
	}

	public final static NetworkSendResult sendPing(InetAddress addr, int timeout, boolean nanosec)
	{
		NetworkSendResult rc = new NetworkSendResult();
		rc.time = -1;
		try
		{
			long ct = nanosec ? System.nanoTime() : System.currentTimeMillis();
			boolean success = addr.isReachable(timeout);
			long ct2 = nanosec ? System.nanoTime() : System.currentTimeMillis();
			if (success)
			{
				rc.success = true;
				rc.time = ct2 - ct;
			}
		}
		catch (Throwable e)
		{
			rc.err = e;
		}
		return rc;
	}

	public static class NetworkSendResult
	{
		public boolean success;
		public long time;
		public Throwable err;
	}
}
