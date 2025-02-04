package bp.service;

import java.util.List;

public interface BPServiceFromWebSearchEngine extends BPServiceFromWeb
{
	List<String> searchHTML(String keyword);
}
