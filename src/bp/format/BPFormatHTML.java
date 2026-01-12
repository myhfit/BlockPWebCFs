package bp.format;

public class BPFormatHTML implements BPFormat
{
	public final static String FORMAT_HTML = "HTML";
	public final static String MIME_TEXT_HTML = "text/html";
	public final static String MIME_APP_HTML = "application/html";

	public String getName()
	{
		return FORMAT_HTML;
	}

	public String[] getExts()
	{
		return new String[] { ".html", ".htm", MIME_TEXT_HTML, MIME_APP_HTML };
	}

	public String getMIME()
	{
		return MIME_TEXT_HTML;
	}
}