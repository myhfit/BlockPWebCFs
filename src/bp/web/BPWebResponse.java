package bp.web;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import bp.format.BPFormat;
import bp.format.BPFormatJSON;
import bp.format.BPFormatManager;
import bp.format.BPFormatText;
import bp.util.JSONUtil;
import bp.util.LockUtil;
import bp.util.TextUtil;

public class BPWebResponse
{
	public volatile Map<String, List<String>> headerfields;
	public volatile ResponseContentHolder content;
	public volatile Throwable err;
	public volatile BPWebOperation op;
	public volatile int responsecode;
	protected volatile WeakReference<BPWebContext> m_context;

	public String getText()
	{
		return content.getText(getEncoding());
	}

	public boolean hasContent()
	{
		return content != null;
	}

	public String getContentType()
	{
		String rc = null;
		if (headerfields != null)
		{
			List<String> strs = headerfields.get("Content-Type");
			if (strs != null)
			{
				if (strs.size() > 0)
					rc = strs.get(0);
			}
		}
		return rc;
	}

	public BPWebContext getWebContext()
	{
		return m_context.get();
	}

	public void setWebContext(BPWebContext context)
	{
		m_context = new WeakReference<BPWebContext>(context);
	}

	public String toString()
	{
		return err != null ? err.toString() : op.toString();
	}

	public String getEncoding()
	{
		return "utf-8";
	}

	public static class ResponseContentHolder
	{
		protected final ByteArrayOutputStream m_bos = new ByteArrayOutputStream();
		protected final ReadWriteLock m_lock = new ReentrantReadWriteLock(true);
		protected volatile boolean m_direct = false;
		protected volatile WeakReference<Consumer<byte[]>> m_out;
		protected volatile boolean m_finished = false;

		public ResponseContentHolder()
		{
		}

		public boolean finished()
		{
			return LockUtil.rwLock(m_lock, false, () -> m_finished);
		}

		public void finish(BPWebContext context, BPWebOperation op, String contenttype, String encoding)
		{
			LockUtil.rwLock(m_lock, true, () ->
			{
				if (op != null && context != null)
				{
					String target = op.getTarget();
					if (target != null)
					{
						if (target.startsWith("$"))
						{
							String key = target.substring(1);
							context.setVar(key, getContent(contenttype, encoding));
						}
					}
				}
				m_finished = true;
			});
		}

		public Object getContent(String contenttype, String encoding)
		{
			String en = encoding;
			if (en == null)
				en = "utf-8";
			BPFormat f = null;
			String ctstr = contenttype;
			if (ctstr != null)
			{
				int vi = ctstr.indexOf(";");
				if (vi > -1)
				{
					String ex = ctstr.substring(vi + 1).trim();
					ctstr = ctstr.substring(0, vi).trim();
					Map<String, String> exmap = TextUtil.getEXWebContentType(ex);
					String exen = exmap.get("charset");
					if (exen != null && exen.length() > 0)
					{
						en = exen;
					}
				}
				f = BPFormatManager.getFormatByExt(ctstr);
			}
			if (f == null)
				f = new BPFormatText();
			String fname = f.getName();
			Object rc = null;
			if (BPFormatText.FORMAT_TEXT.equals(fname))
			{
				rc = TextUtil.toString(m_bos.toByteArray(), en);
			}
			else if (BPFormatJSON.FORMAT_JSON.equals(fname))
			{
				rc = JSONUtil.decode(TextUtil.toString(m_bos.toByteArray(), en));
			}
			return rc;
		}

		public String getText(String encoding)
		{
			return TextUtil.toString(m_bos.toByteArray(), encoding);
		}

		public byte[] getByteArray()
		{
			return m_bos.toByteArray();
		}

		public void write(byte[] arr, int offset, int len)
		{
			LockUtil.rwLock(m_lock, false, () ->
			{
				if (m_direct)
				{
					if (offset == 0 && arr.length == len)
					{
						m_out.get().accept(arr);
					}
					else
					{
						byte[] ra = new byte[len];
						System.arraycopy(arr, offset, ra, 0, len);
						m_out.get().accept(ra);
					}
				}
				else
				{
					m_bos.write(arr, offset, len);
				}
			});
		}

		public void redirect(Consumer<byte[]> out)
		{
			LockUtil.rwLock(m_lock, true, () ->
			{
				m_out = new WeakReference<>(out);
				out.accept(m_bos.toByteArray());
			});
		}
	}
}
