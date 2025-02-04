package bp.data;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import static bp.util.TPF.*;

public class BPHTMLElement
{
	protected List<BPHTMLElement> m_children;

	protected BPHTMLElement m_parent;

	protected Map<String, Object> m_attributes;

	protected String m_classname;

	protected BPHTMLElementTag m_tag;

	public void parse(String text, int pos) throws ParseException
	{
		int p = pos;
		if (text.charAt(p) == '<')
		{
			int[] vi = indexOf(text, new String[] { " ", ">", "/" }, p);
			int vi1 = vi[0];
			if (vi1 == -1)
			{
				throw new ParseException(text, p);
			}
			if (vi1 > p + 1)
			{
				String tag = text.substring(p + 1, vi1);
				if (tag.equalsIgnoreCase("!DOCTYPE"))
					m_tag = BPHTMLElementTag.DOCTYPE;
				else
				{
					BPHTMLElementTag[] alltags= BPHTMLElementTag.values();
					for(BPHTMLElementTag t:alltags)
					{
						if(t.name().equals(tag.toLowerCase()))
						{
							m_tag=t;
							break;
						}
					}
					if(m_tag==null)
						m_tag=BPHTMLElementTag.UNKNOWN;
				}
			}
		}
	}

	public BPHTMLElementTag getTag()
	{
		return m_tag;
	}

	public String getHTMLString()
	{
		return m_tag.getStart(m_attributes) + getInnerHTML() + m_tag.getEnd();
	}

	public String getInnerHTML()
	{
		StringBuilder sb = new StringBuilder();
		if (m_children != null)
		{
			for (BPHTMLElement child : m_children)
			{
				sb.append(child.getHTMLString());
			}
		}
		return sb.toString();
	}

	public void appendHTMLString(StringBuilder sb, int indent)
	{
		if (indent > 0)
		{
			startIndent(sb, indent);
		}
		sb.append(m_tag.getStart(m_attributes));
		if (m_children != null)
		{
			boolean neednewline = indent > -1;
			for (BPHTMLElement child : m_children)
			{
				if (neednewline)
				{
					sb.append("\n");
				}
				child.appendHTMLString(sb, indent + 1);
			}
			String end = m_tag.getEnd();
			if (neednewline)
			{
				sb.append("\n");
				startIndent(sb, indent);
			}
			if (end.length() > 0)
			{
				sb.append(end);
				if (neednewline)
				{
					sb.append("\n");
				}
			}
		}
	}

	protected final static void startIndent(StringBuilder sb, int indent)
	{
		for (int i = 0; i < indent; i++)
			sb.append(indent);
	}
}
