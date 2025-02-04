package bp.web;

import java.util.Map;

import bp.data.BPMData;

public interface BPWebOperation extends BPMData
{
	String getVerb();

	String[][] getHeaderFields();

	byte[] getContent();

	String getContentText();

	String getProtocol();

	String getPath();

	String getQuery();

	String getTarget();

	boolean needTransContent();

	void transContent(Map<String, Object> vars);
	
	String toLineText();

	public enum CommonHTTPVerbs
	{
		GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS;

		public final static String[] verbs()
		{
			CommonHTTPVerbs[] vs = values();
			String[] rc = new String[values().length];
			for (int i = 0; i < rc.length; i++)
			{
				rc[i] = vs[i].name();
			}
			return rc;
		}
	}
}
