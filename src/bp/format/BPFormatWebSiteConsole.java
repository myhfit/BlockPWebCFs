package bp.format;

public class BPFormatWebSiteConsole implements BPFormat
{
	public final static String FORMAT_WEBSITECONSOLE = "WebSite Console";

	public String getName()
	{
		return FORMAT_WEBSITECONSOLE;
	}

	public String[] getExts()
	{
		return new String[] { ".wsconsole" };
	}
}