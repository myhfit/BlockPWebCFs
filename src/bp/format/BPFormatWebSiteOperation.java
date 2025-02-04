package bp.format;

public class BPFormatWebSiteOperation implements BPFormat
{
	public final static String FORMAT_WEBSITEOP = "WebSite Operation";

	public String getName()
	{
		return FORMAT_WEBSITEOP;
	}

	public String[] getExts()
	{
		return new String[] { ".wsop" };
	}
}
