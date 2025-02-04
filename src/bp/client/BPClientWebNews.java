package bp.client;

import java.util.List;
import java.util.Map;

public interface BPClientWebNews extends BPClientWeb
{
	public final static String CATEGORY_WEBNEWS= "WebNews";

	List<Map<String, Object>> getNews();
}