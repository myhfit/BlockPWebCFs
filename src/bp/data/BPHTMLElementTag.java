package bp.data;

import java.util.Map;
import java.util.Map.Entry;

public enum BPHTMLElementTag
{
	NULL(), DOCTYPE("!DOCTYPE html", false), html(true), head(true), body(true), form(true), img(false), 
	table(true,true), tr(true), td(true), th(true), col(true),
	input(false), frame(true), iframe(true), button(true), canvas(true), center(true), a(true), article(true), h1(true), h2(true), h3(true), 
	h4(true), h5(true), h6(true), b(true), br(true), fieldset(true), legend(true), label(true,true), p(true), title(true), meta(false), div(true,true),
	textarea(true,true), link(false), nav(true,true), script(true,true), select(true,true), option(true,true),progress(true,true),UNKNOWN(true);

	private boolean m_hasend;
	private boolean m_explicitend;
	private String m_name;

	private final static String START = "<";
	private final static String END = ">";
	private final static String SLASH = "/";
	private final static String EQUALS = "=";
	private final static String QUOTE = "'";

	private BPHTMLElementTag()
	{
	}

	private BPHTMLElementTag(boolean hasend)
	{
		this(hasend,false);
	}
	
	private BPHTMLElementTag(boolean hasend,boolean explicitend)
	{
		m_name = this.name();
		m_hasend = hasend;
		m_explicitend=explicitend;
	}

	private BPHTMLElementTag(String name, boolean hasend)
	{
		m_name = name;
		m_hasend = hasend;
	}

	public boolean isNULL()
	{
		return m_name == null;
	}
	
	public boolean isExplicitEnd()
	{
		return m_explicitend;
	}

	public String getStart(Map<String, Object> params)
	{
		return getStart(params, false);
	}

	public String getStart(Map<String, Object> params, boolean endflag)
	{
		if (m_name == null)
			return "";

		if (params == null || params.size() == 0)
		{
			return START + m_name + END;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(START + m_name);
		for (Entry<String, Object> entry : params.entrySet())
		{
			sb.append(" " + entry.getKey() + EQUALS + QUOTE + entry.getValue() + QUOTE);
		}
		sb.append((endflag && m_hasend && (!m_explicitend)) ? SLASH + END : END);
		return sb.toString();
	}

	public String getEnd()
	{
		if (m_name == null)
			return "";

		return m_hasend ? START + SLASH + m_name + END : "";
	}
}
