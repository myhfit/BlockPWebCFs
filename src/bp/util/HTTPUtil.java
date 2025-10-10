package bp.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import bp.web.BPWebResponse;
import bp.web.BPWebResponse.ResponseContentHolder;

public class HTTPUtil
{
	public final static CompletionStage<BPWebResponse> getAsync(String url, Map<String, String> headermap)
	{
		return CompletableFuture.supplyAsync(() -> get(url, headermap));
	}

	public final static CompletionStage<BPWebResponse> postAsync(String url, byte[] content, Map<String, String> headermap)
	{
		return CompletableFuture.supplyAsync(() -> post(url, content, headermap));
	}

	public final static BPWebResponse get(String url, Map<String, String> headermap)
	{
		return send("GET", url, null, transHeaderMapToArray(headermap), null);
	}

	public final static BPWebResponse post(String url, byte[] content, Map<String, String> headermap)
	{
		return send("POST", url, content, transHeaderMapToArray(headermap), null);
	}

	public final static BPWebResponse send(String verb, String urlstr, byte[] content, String[][] headerfields, BiConsumer<HttpURLConnection, byte[]> cb)
	{
		BPWebResponse rc = null;
		HttpURLConnection conn = null;
		rc = new BPWebResponse();
		boolean needcb = cb != null;
		try
		{
			URL url = new URL(urlstr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(verb);
			if (headerfields != null && headerfields.length > 0)
			{
				for (String[] headerfield : headerfields)
				{
					conn.setRequestProperty(headerfield[0], headerfield[1]);
				}
			}
			conn.setDoInput(true);
			conn.setDoOutput(true);
			if (content != null && content.length > 0)
			{
				OutputStream out = conn.getOutputStream();
				out.write(content);
				out.flush();
			}
			int c = conn.getResponseCode();
			if (c > -1)
			{
				rc.responsecode = c;
				byte[] bs = new byte[65536];
				ResponseContentHolder contentholder = new ResponseContentHolder();
				contentholder.setup(conn.getContentType(), conn.getContentEncoding());
				rc.content = contentholder;
				rc.headerfields = conn.getHeaderFields();
				try (InputStream in = (c == 200 ? conn.getInputStream() : conn.getErrorStream() == null ? conn.getInputStream() : conn.getErrorStream()))
				{
					int len = in.read(bs);
					while (len >= 0)
					{
						contentholder.write(bs, 0, len);
						if (needcb)
							cb.accept(conn, BSUtil.newBS(bs, c, len));
						len = in.read(bs);
					}
					contentholder.finish(null, null);
				}
			}
		}
		catch (IOException e)
		{
			rc.err = e;
		}
		finally
		{
			if (conn != null)
			{
				conn.disconnect();
			}
		}
		return rc;
	}

	public final static String[][] transHeaderMapToArray(Map<String, String> headermap)
	{
		String[][] rc = null;
		if (headermap != null)
		{
			rc = new String[headermap.size()][2];
			int i = 0;
			for (Entry<String, String> entry : headermap.entrySet())
			{
				String key = entry.getKey();
				String value = entry.getValue();
				rc[i][0] = key;
				rc[i][1] = value;
				i++;
			}
		}
		return rc;
	}

	public static interface ResponseFilter
	{
		boolean filter(BPWebResponse resp);
	}

	public static class CookieResponseFilter implements ResponseFilter
	{
		protected Map<String, String> m_header;

		public CookieResponseFilter(Map<String, String> header)
		{
			m_header = header;
		}

		public boolean filter(BPWebResponse resp)
		{
			return false;
		}
	}

	public static abstract class ResponseHandlerBase<T> implements BiFunction<BPWebResponse, Throwable, T>
	{
		protected ResponseFilter[] m_filters;

		public ResponseHandlerBase(ResponseFilter... filters)
		{
			m_filters = filters;
		}
	}

	public static class RespJsonHandler<T> extends ResponseHandlerBase<T>
	{
		public RespJsonHandler(ResponseFilter... filters)
		{
			super(filters);
		}

		public T apply(BPWebResponse resp, Throwable u)
		{
			if (resp.err != null)
				throw new RuntimeException(resp.err);
			String text = resp.getText();
			if (text != null)
				return JSONUtil.decode(text);
			return null;
		}
	}
}
