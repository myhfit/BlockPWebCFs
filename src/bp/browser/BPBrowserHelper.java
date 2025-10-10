package bp.browser;

import java.util.Map;

import bp.handler.BPHandler;

public interface BPBrowserHelper extends BPHandler<String>
{
	int open(String browser, String url, Map<String, Object> options);

	boolean canHandle(String browser);
}
