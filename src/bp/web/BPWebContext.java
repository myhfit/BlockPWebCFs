package bp.web;

import java.util.Map;
import java.util.concurrent.CompletionStage;

import bp.res.BPResourceWebSiteLink;

public interface BPWebContext
{
	CompletionStage<Void> setWebSite(BPResourceWebSiteLink link);

	CompletionStage<BPWebResponse> operate(BPWebOperation op);

	Map<String, Object> getVarContext();

	String[][] getHeaderFields();

	void setHeaderField(String[] headerfield);

	void removeHeaderField(String headerfieldkey);

	void shutdown();

	void setVar(String key, Object value);

	void removeVar(String key);

	Object getVar(String key);
}
