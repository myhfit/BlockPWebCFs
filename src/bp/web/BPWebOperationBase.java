package bp.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bp.util.JSONUtil;
import bp.util.ObjUtil;
import bp.util.ScriptUtil;
import bp.util.TextUtil;
import bp.util.TextUtil.EscapeStringScanner;

public class BPWebOperationBase implements BPWebOperation
{
	public volatile String protocol;
	public volatile String verb;
	public volatile String[][] headerfields;
	public volatile byte[] content;
	public volatile String encoding;
	public volatile String host;
	public volatile String path;
	public volatile String query;
	public volatile String target;
	public volatile boolean needtranscontent;

	public boolean needTransContent()
	{
		return needtranscontent;
	}

	public void transContent(Map<String, Object> vars)
	{
		setContent(ScriptUtil.transContent(getContentText(), vars));
	}

	public String getVerb()
	{
		return verb;
	}

	public String getTarget()
	{
		return target;
	}

	public String[][] getHeaderFields()
	{
		return headerfields;
	}

	public byte[] getContent()
	{
		return content;
	}

	public String getContentText()
	{
		String en = getEncoding();
		String rc = null;
		if (content != null)
		{
			rc = TextUtil.toString(content, en);
		}
		return rc;
	}

	public String getEncoding()
	{
		return encoding == null ? "ASCII" : encoding;
	}

	public String toString()
	{
		return verb + (path != null ? (" " + path) : "");
	}

	public String getProtocol()
	{
		return protocol;
	}

	public String getPath()
	{
		return path;
	}

	public String getQuery()
	{
		return query;
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		rc.put("path", path);
		rc.put("verb", verb);
		rc.put("content", content);
		rc.put("contentText", getContentText());
		rc.put("target", target);
		Map<String, Object> options = new HashMap<String, Object>();
		List<List<String>> headerlist;
		if (headerfields != null)
		{
			headerlist = new ArrayList<List<String>>();
			for (String[] hf : headerfields)
			{
				headerlist.add(Arrays.asList(hf));
			}
			options.put("header", headerlist);
		}
		rc.put("options", options);
		return rc;
	}

	@SuppressWarnings("unchecked")
	public void setMappedData(Map<String, Object> data)
	{
		path = ObjUtil.toString(data.get("path"));
		verb = ObjUtil.toString(data.get("verb"));
		target = ObjUtil.toString(data.get("target"));
		Map<String, Object> options = (Map<String, Object>) data.get("options");
		if (options != null)
		{
			setHeader((List<List<String>>) options.get("header"));
		}
		configByHeaderFields();
		setContent(data.get("content"));
	}

	public void setHeader(List<List<String>> headerfields)
	{
		String[][] hs = null;
		if (headerfields != null)
		{
			hs = new String[headerfields.size()][2];
			int l = headerfields.size();
			for (int i = 0; i < l; i++)
			{
				List<String> h = headerfields.get(i);
				hs[i][0] = h.get(0);
				hs[i][1] = h.get(1);
			}
		}
		this.headerfields = hs;
	}

	public void setContent(Object content)
	{
		byte[] bs = null;
		if (content != null)
		{
			if (content instanceof String)
			{
				bs = encodeTextContent((String) content, "", getEncoding());
			}
		}
		this.content = bs;
	}

	protected final static byte[] encodeTextContent(String text, String contenttype, String textencoding)
	{
		byte[] bs = null;
		bs = TextUtil.fromString(text, textencoding);
		return bs;
	}

	public final static BPWebOperationBuider build(String verb)
	{
		BPWebOperationBuider builder = new BPWebOperationBuider();
		builder.setVerb(verb);
		return builder;
	}

	public final static BPWebOperationBase parse(String line)
	{
		BPWebOperationBase rc = null;
		Map<String, Object> map = new HashMap<String, Object>();
		rc = new BPWebOperationBase();
		int l = line.length();
		if (l > 0)
		{
			int vi0 = line.indexOf(" ");
			String verb = vi0 > 0 ? line.substring(0, vi0) : line;
			map.put("verb", verb);
			if (verb.startsWith("!") || verb.startsWith("_"))
				rc.encoding = "utf-8";
			if (vi0 > 0 && vi0 < (l - 1))
			{
				int vi1 = line.indexOf(" ", vi0 + 1);
				map.put("path", vi1 <= vi0 ? line.substring(vi0 + 1) : line.substring(vi0 + 1, vi1));
				if (vi1 > vi0 && vi1 < l - 1)
				{
					char c = line.charAt(vi1 + 1);
					if (c == '=')
					{
						vi1++;
						if (vi1 < l - 1)
						{
							c = line.charAt(vi1 + 1);
							if (c == '"')
							{
								rc.needtranscontent = true;
							}
						}
						else
						{
							c = ' ';
						}
					}
					if (c == '"')
					{
						EscapeStringScanner scanner = new EscapeStringScanner(line);
						String p = scanner.scan(vi1 + 2);
						if (p != null)
						{
							map.put("content", p);
							int vi2 = scanner.endpos + 1;
							if (vi2 < l - 1 && line.charAt(vi2) == ' ')
							{
								int vi3 = line.indexOf(" ", vi2 + 1);
								if (vi3 >= 0)
								{
									String target = line.substring(vi2 + 1, vi3);
									map.put("target", target);

									String p4 = line.substring(vi3 + 1);
									Map<String, Object> options = JSONUtil.decode(p4);
									map.put("options", options);
								}
							}
						}
					}
					else if (c == ' ')
					{
						int vi2 = vi1;
						if (vi2 < l - 1 && line.charAt(vi2) == ' ')
						{
							int vi3 = line.indexOf(" ", vi2 + 1);
							if (vi3 >= 0)
							{
								String target = line.substring(vi2 + 1, vi3);
								map.put("target", target);

								String p4 = line.substring(vi3 + 1);
								Map<String, Object> options = JSONUtil.decode(p4);
								map.put("options", options);
							}
						}
					}
				}
			}
		}
		rc.setMappedData(map);
		return rc;
	}

	protected void configByHeaderFields()
	{
		if (headerfields != null)
		{
			String[][] hfs = headerfields;
			for (String[] hf : hfs)
			{
				String key = hf[0].toLowerCase();
				if ("content-type".equals(key))
				{
					String v = hf[1];
					if (v != null)
					{
						String[] vs = v.split(";");
						for (String vi : vs)
						{
							if (vi.trim().startsWith("charset"))
							{
								String[] chs = vi.split("=");
								if (chs.length > 1)
								{
									if ("charset".equals(chs[0].trim()))
									{
										encoding = chs[1].trim();
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public static class BPWebOperationBuider
	{
		protected BPWebOperationBase m_op;

		public BPWebOperationBuider()
		{
			m_op = new BPWebOperationBase();
		}

		public BPWebOperation getOperation()
		{
			return m_op;
		}

		public BPWebOperationBuider setVerb(String verb)
		{
			m_op.verb = verb;
			return this;
		}

		public BPWebOperationBuider setProtocol(String protocol)
		{
			m_op.protocol = protocol;
			return this;
		}

		public BPWebOperationBuider setHeaderFields(String[][] headerfields)
		{
			m_op.headerfields = headerfields;
			return this;
		}

		public BPWebOperationBuider setContent(byte[] content)
		{
			m_op.content = content;
			return this;
		}

		public BPWebOperationBuider setEncoding(String encoding)
		{
			m_op.encoding = encoding;
			return this;
		}

		public BPWebOperationBuider setPath(String path)
		{
			m_op.path = path;
			return this;
		}

		public BPWebOperationBuider setQuery(String query)
		{
			m_op.query = query;
			return this;
		}

		public BPWebOperationBuider setContentText(String text)
		{
			m_op.content = TextUtil.fromString(text, m_op.encoding);
			return this;
		}
	}

	public String toLineText()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(verb);
		sb.append(" ");
		if (path != null)
			sb.append(path);
		sb.append(" ");
		String content = getContentText();
		if (content != null)
		{
			if (needtranscontent)
			{
				sb.append("=\"");
				sb.append(TextUtil.escape(content));
				sb.append("\"");
			}
			else
			{
				sb.append("\"");
				sb.append(TextUtil.escape(content));
				sb.append("\"");
			}
		}
		sb.append(" ");
		if (target != null)
		{
			sb.append(target);
		}
		sb.append(" ");
		if (headerfields != null)
		{
			Map<String, Object> options = new HashMap<String, Object>();
			List<List<String>> headerlist;
			if (headerfields != null)
			{
				headerlist = new ArrayList<List<String>>();
				for (String[] hf : headerfields)
				{
					headerlist.add(Arrays.asList(hf));
				}
				options.put("header", headerlist);
			}
			sb.append(JSONUtil.encode(options));
		}
		return sb.toString().trim();
	}
}
