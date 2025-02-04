package bp.format;

public class BPFormatWebSiteLink implements BPFormat
{
	public final static String FORMAT_WEBSITELINK = "WebSite Link";

	public String getName()
	{
		return FORMAT_WEBSITELINK;
	}

	public String[] getExts()
	{
		return new String[] { ".bpwslink" };
	}

}
