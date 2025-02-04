package bp.format;

public class BPFormatHTML implements BPFormat
{
	public final static String FORMAT_HTML = "HTML";

	public String getName()
	{
		return FORMAT_HTML;
	}

	public String[] getExts()
	{
		return new String[] { ".html", ".htm", "text/html" };
	}
}