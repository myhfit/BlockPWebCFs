package bp.client;

public interface BPClientWebSite extends BPClientWeb
{
	public final static String CATEGORY_WEBSITE = "WebSite";

	public static interface BPClientWebSiteContent
	{
		String getContentType();
	}

	public static interface BPClientWebSiteEndpoint<SITE extends BPClientWebSite>
	{
		String getName();

		SITE getSite();

		<C extends BPClientWebSiteContent> C getContent();
	}
}
