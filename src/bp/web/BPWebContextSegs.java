package bp.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;

import bp.BPCore;
import bp.res.BPResourceWebSiteLink;
import bp.script.BPScript;
import bp.script.BPScriptManager;
import bp.script.BPScriptRuntime;
import bp.util.ObjUtil;
import bp.util.Std;
import bp.util.TextUtil;
import bp.web.BPWebContextBase.WebSeg;
import bp.web.BPWebContextBase.WebThread;
import bp.web.BPWebResponse.ResponseContentHolder;

public class BPWebContextSegs
{
	public final static class BPWebContextSegChangeWebSiteLink extends WebSeg<Void>
	{
		protected volatile BPResourceWebSiteLink m_link;

		public BPWebContextSegChangeWebSiteLink(BPResourceWebSiteLink link)
		{
			m_link = link;
		}

		public Void get()
		{
			getThread().setWebSiteLink(m_link);
			return null;
		}
	}

	public final static class BPWebContextSegOperate extends BPWebContextSegRun<BPWebResponse>
	{
		protected volatile BPWebOperation m_op;

		public BPWebContextSegOperate(BPWebOperation op)
		{
			m_op = op;
		}

		public BPWebResponse get()
		{
			WebThread thread = getThread();
			disconnect(thread);
			BPWebOperation op = m_op;
			String verb = op.getVerb();
			BPWebContext context = thread.getWebContext();
			if (op.needTransContent())
			{
				op.transContent(context.getVarContext());
			}
			if ("!SETCONTEXT".equals(verb))
			{
				return setContext(thread, op);
			}
			else if ("!PREPARESCRIPT".equals(verb))
			{
				return prepareScript(thread, op);
			}
			else if ("!PREPARESCRIPTRUNTIME".equals(verb))
			{
				return prepareScriptRuntime(thread, op);
			}
			else if (verb.startsWith("_"))
			{
				return scriptCall(thread, op);
			}
			else if (verb.startsWith("//"))
			{
				return null;
			}
			connect(thread, op);
			HttpURLConnection conn = thread.getConnection();
			String[][] headerfields = makeRequestHeaderFields(thread, op);
			BPWebResponse rc = new BPWebResponse();
			rc.op = op;
			rc.setWebContext(context);
			byte[] reqcontent = op.getContent();
			try
			{
				conn.setRequestMethod(verb);
				if (headerfields != null && headerfields.length > 0)
				{
					for (String[] headerfield : headerfields)
					{
						conn.setRequestProperty(headerfield[0], headerfield[1]);
						Std.debug(headerfield[0] + "=" + headerfield[1]);
					}
				}
				conn.setDoInput(true);
				conn.setDoOutput(true);
				if (reqcontent != null && reqcontent.length > 0)
				{
					OutputStream out = conn.getOutputStream();
					out.write(reqcontent);
					Std.debug(TextUtil.toString(reqcontent, "ASCII"));
					out.flush();
				}
				int c = conn.getResponseCode();
				if (c > -1)
				{
					rc.responsecode = c;
					byte[] bs = new byte[65536];
					ResponseContentHolder content = new ResponseContentHolder();
					rc.content = content;
					rc.headerfields = conn.getHeaderFields();
					try (InputStream in = (c == 200 ? wrapEncoding(conn.getInputStream(), rc.headerfields) : conn.getErrorStream()))
					{
						int len = in.read(bs);
						while (len >= 0)
						{
							content.write(bs, 0, len);
							len = in.read(bs);
						}
						content.finish(context, op, conn.getContentType(), conn.getContentEncoding());
					}
				}
			}
			catch (IOException e)
			{
				rc.err = e;
			}
			catch (Exception e)
			{
				Std.err(e);
			}
			finally
			{
				disconnect(thread);
			}
			return rc;
		}
	}

	public static abstract class BPWebContextSegRun<V> extends WebSeg<V>
	{
		protected final static InputStream wrapEncoding(InputStream in, Map<String, List<String>> headerfields) throws IOException
		{
			List<String> encoding = getHeaderValues(headerfields, "Content-Encoding");
			if (encoding != null && encoding.size() > 0)
			{
				String en = encoding.get(0);
				if ("gzip".equals(en))
				{
					return new GZIPInputStream(in);
				}
				else if ("deflate".equals(en))
				{
					return new DeflaterInputStream(in);
				}
			}
			return in;
		}

		protected final static List<String> getHeaderValues(Map<String, List<String>> headerfields, String key)
		{
			key = key.toUpperCase();
			for (String k : headerfields.keySet())
			{
				if (k != null && k.toUpperCase().equals(key))
					return headerfields.get(k);
			}
			return null;
		}

		protected final static Boolean connect(WebThread thread, BPWebOperation op)
		{
			BPResourceWebSiteLink link = thread.getWebSiteLink();
			Map<String, Object> linkmap = link.getMappedData();
			String host = (String) linkmap.get("host");
			Integer port = ObjUtil.toInt(linkmap.get("port"), null);
			String basepath = (String) linkmap.get("basepath");
			String auth = "";
			{
				String user = (String) linkmap.get("user");
				String pass = (String) linkmap.get("password");
				if (user != null && user.length() > 0)
				{
					auth = user;
					if (pass != null && pass.length() > 0)
						auth += ":" + pass;
					auth += "@";
				}
			}
			URL url = null;
			try
			{
				url = new URL(link.getProtocol() + "://" + auth + host + (port == null ? "" : (":" + port)) + (basepath == null ? "" : basepath) + op.getPath());
			}
			catch (MalformedURLException e)
			{
				Std.err(e);
				throw new RuntimeException(e);
			}
			try
			{
				Std.debug(url.toString());
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				thread.setConnection(conn);
				return true;
			}
			catch (IOException e)
			{
				Std.err(e);
				throw new RuntimeException(e);
			}
		}

		protected final static BPWebResponse setContext(WebThread thread, BPWebOperation op)
		{
			BPWebResponse rc = new BPWebResponse();
			rc.op = op;
			String[][] headers = op.getHeaderFields();
			BPWebContext context = thread.getWebContext();
			rc.setWebContext(context);
			if (headers != null)
			{
				for (String[] h : headers)
				{
					context.setHeaderField(h);
				}
			}
			return rc;
		}

		protected final static BPWebResponse scriptCall(WebThread thread, BPWebOperation op)
		{
			BPWebResponse rc = new BPWebResponse();
			rc.op = op;
			BPScriptRuntime runtime = thread.getScriptRuntime();
			runtime.runScript(op.getVerb().substring(1));
			return rc;
		}

		protected final static BPWebResponse prepareScript(WebThread thread, BPWebOperation op)
		{
			BPWebResponse rc = new BPWebResponse();
			rc.op = op;
			BPScriptManager man = BPCore.getWorkspaceContext().getScriptManager();
			BPScript sc = man.getScript(op.getContentText());
			BPScriptRuntime runtime = thread.getScriptRuntime();
			runtime.loadScript(sc);
			return rc;
		}

		protected final static BPWebResponse prepareScriptRuntime(WebThread thread, BPWebOperation op)
		{
			BPWebResponse rc = new BPWebResponse();
			rc.op = op;
			BPScriptRuntime runtime = thread.getScriptRuntime();
			runtime.setupRuntime(op.getContentText());
			return rc;
		}

		protected final static String[][] makeRequestHeaderFields(WebThread thread, BPWebOperation op)
		{
			List<String[]> hfs = new ArrayList<String[]>();
			Map<String, String[]> hfmap = new HashMap<String, String[]>();
			BPWebContext context = thread.getWebContext();
			String[][] contexthfs = context.getHeaderFields();
			for (String[] hf : contexthfs)
			{
				hfs.add(hf);
				hfmap.put(hf[0], hf);
			}
			String[][] ophfs = op.getHeaderFields();
			if (ophfs != null)
			{
				for (String[] hf : ophfs)
				{
					String k = hf[0];
					if (hfmap.containsKey(k))
					{
						hfmap.get(k)[1] = hf[1];
					}
					else
					{
						hfs.add(hf);
						hfmap.put(hf[0], hf);
					}
				}
			}
			return hfs.toArray(new String[hfs.size()][]);
		}

		protected final static Boolean disconnect(WebThread thread)
		{
			HttpURLConnection conn = thread.getConnection();
			if (conn != null)
			{
				thread.setConnection(null);
				try
				{
					conn.disconnect();
				}
				finally
				{
				}
			}
			return true;
		}
	}
}
