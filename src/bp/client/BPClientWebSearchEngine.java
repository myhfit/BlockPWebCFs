package bp.client;

import java.util.List;
import java.util.Map;

public interface BPClientWebSearchEngine extends BPClientWeb
{
	public final static String CATEGORY_WEBSEARCHENGINE = "WebSearchEngine";

	List<Map<String, Object>> searchHTML(String keyword);
}
