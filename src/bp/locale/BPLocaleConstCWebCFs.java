package bp.locale;

//Computer Web Formats
public enum BPLocaleConstCWebCFs implements BPLocaleConstDirect
{
	TORRENT_FILE("Torrent File"),
	MAGNET,
	;

	public final static String PACK_COMPUTER_WEBCFS = "c_webcfs";

	private String m_value;

	public String getPackName()
	{
		return PACK_COMPUTER_WEBCFS;
	}

	private BPLocaleConstCWebCFs()
	{
	}

	private BPLocaleConstCWebCFs(String v)
	{
		m_value = v;
	}

	public String getValue(int flag)
	{
		return m_value == null ? getNormalName() : m_value;
	}
}
