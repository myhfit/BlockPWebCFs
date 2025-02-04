package bp.service;

public interface BPServiceFromWeb extends BPService
{
	String getBaseURL();

	void setBaseURL(String baseurl);
}
