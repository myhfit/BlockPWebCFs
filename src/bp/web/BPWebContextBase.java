package bp.web;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import bp.res.BPResourceWebSiteLink;
import bp.script.BPScriptRuntime;

public class BPWebContextBase implements BPWebContext
{
	protected ExecutorService m_exec;
	protected volatile AtomicBoolean m_stoprunflag = new AtomicBoolean(false);
	protected volatile Map<String, Object> m_varcontext;
	protected volatile CopyOnWriteArrayList<String[]> m_headerfields;

	public BPWebContextBase(BPResourceWebSiteLink link)
	{
		m_exec = Executors.newSingleThreadExecutor(new WebThreadFactory(link, m_stoprunflag, this));
		m_varcontext = new ConcurrentHashMap<String, Object>();
		m_headerfields = new CopyOnWriteArrayList<String[]>(new String[][] { new String[] { "Accept", "*/*" } });
	}

	public void shutdown()
	{
		m_exec.shutdown();
	}

	public CompletionStage<Void> setWebSite(BPResourceWebSiteLink link)
	{
		return CompletableFuture.supplyAsync(new BPWebContextSegs.BPWebContextSegChangeWebSiteLink(link), m_exec);
	}

	public Map<String, Object> getVarContext()
	{
		return m_varcontext;
	}

	public void setVar(String key, Object value)
	{
		m_varcontext.put(key, value);
	}

	public void removeVar(String key)
	{
		m_varcontext.remove(key);
	}

	public Object getVar(String key)
	{
		return m_varcontext.get(key);
	}

	public String[][] getHeaderFields()
	{
		return m_headerfields.toArray(new String[m_headerfields.size()][]);
	}

	public void setHeaderField(String[] headerfield)
	{
		String k = headerfield[0];
		String v = headerfield[1];
		for (String[] hf : m_headerfields)
		{
			if (k.equals(hf[0]))
			{
				hf[1] = v;
				return;
			}
		}
		m_headerfields.add(headerfield);
	}

	public void removeHeaderField(String headerfieldkey)
	{
		for (int i = 0; i < m_headerfields.size(); i++)
		{
			String[] hf = m_headerfields.get(i);
			if (headerfieldkey.equals(hf[0]))
			{
				m_headerfields.remove(i);
				return;
			}
		}
	}

	public CompletionStage<BPWebResponse> operate(BPWebOperation op)
	{
		return CompletableFuture.supplyAsync(new BPWebContextSegs.BPWebContextSegOperate(op), m_exec);
	}

	protected class WebThreadFactory implements ThreadFactory
	{
		protected volatile BPResourceWebSiteLink m_link;
		protected WeakReference<AtomicBoolean> m_stoprunflag;
		protected WeakReference<BPWebContext> m_contextref;

		public WebThreadFactory(BPResourceWebSiteLink link, AtomicBoolean stoprunflag, BPWebContext context)
		{
			m_link = link;
			m_stoprunflag = new WeakReference<AtomicBoolean>(stoprunflag);
			m_contextref = new WeakReference<BPWebContext>(context);
		}

		public Thread newThread(Runnable r)
		{
			Thread t = new WebThread(m_link, r, m_stoprunflag, m_contextref.get());
			return t;
		}
	}

	static class WebThread extends Thread
	{
		protected volatile BPResourceWebSiteLink m_link;
		protected volatile HttpURLConnection m_conn;
		protected WeakReference<AtomicBoolean> m_stoprunflag;
		protected volatile WeakReference<BPWebContext> m_contextref;
		protected BPScriptRuntime m_scruntime;

		public WebThread(BPResourceWebSiteLink link, Runnable r, WeakReference<AtomicBoolean> stoprunflag, BPWebContext context)
		{
			super(r);
			m_link = link;
			m_stoprunflag = stoprunflag;
			m_contextref = new WeakReference<BPWebContext>(context);
			setDaemon(true);
		}

		public BPScriptRuntime getScriptRuntime()
		{
			if (m_scruntime == null)
			{
				Map<String, Object> envs = new ConcurrentHashMap<String, Object>();
				BPWebContext context=m_contextref.get();
				WeakReference<BPWebContext> cr = new WeakReference<BPWebContext>(context);
				envs.put("webcontext", cr);
				m_scruntime = new BPScriptRuntime(envs, context.getVarContext());
			}
			return m_scruntime;
		}

		public BPResourceWebSiteLink getWebSiteLink()
		{
			return m_link;
		}

		public void setWebSiteLink(BPResourceWebSiteLink link)
		{
			m_link = link;
		}

		public HttpURLConnection getConnection()
		{
			return m_conn;
		}

		public void setConnection(HttpURLConnection connection)
		{
			m_conn = connection;
		}

		public BPWebContext getWebContext()
		{
			return m_contextref.get();
		}
	}

	abstract static class WebSeg<V> implements Supplier<V>
	{
		protected WebThread getThread()
		{
			return (WebThread) Thread.currentThread();
		}

		protected BPResourceWebSiteLink getWebSiteLink()
		{
			return getThread().getWebSiteLink();
		}
	}
}
